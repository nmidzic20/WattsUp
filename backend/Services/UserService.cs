using backend.Data;
using backend.Models.Entities;
using backend.Models.Requests;
using backend.Models.Responses;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;

namespace backend.Services
{
    public class UserService
    {
        private readonly DatabaseContext _context;
        private readonly IConfiguration _configuration;

        public UserService(DatabaseContext context, IConfiguration configuration)
        {
            _context = context;
            _configuration = configuration;
        }

        public async Task<User> CreateUserAsync(UserCreateRequest userRequest)
        {
            var existingUser = await _context.User.FirstOrDefaultAsync(u => u.Email == userRequest.Email);
            if (existingUser != null)
            {
                return null;
            }

            User newUser = new User
            {
                FirstName = userRequest.FirstName,
                LastName = userRequest.LastName,
                Email = userRequest.Email,
                Password = userRequest.Password,
                Active = true,
                CreatedAt = DateTime.UtcNow,
                RoleId = 2
            };

            newUser = HashPassword(newUser);

            return newUser;
        }

        private User HashPassword(User newUser)
        {
            var passwordHasher = new PasswordHasher<User>();
            string hashedPassword = passwordHasher.HashPassword(newUser, newUser.Password);
            newUser.Password = hashedPassword;
            return newUser;
        }

        private PasswordVerificationResult VerifyHashedPassword(User user, string password)
        {
            var passwordHasher = new PasswordHasher<User>();
            return passwordHasher.VerifyHashedPassword(user, user.Password, password);
        }

        public async Task<LoginResponse> LoginAsync(UserLoginRequest userRequest)
        {
            var user = await _context.User.Include(r => r.Role).Include(t => t.RefreshToken).FirstOrDefaultAsync(u => u.Email == userRequest.Email);
            if (user == null)
            {
                throw new Exception("User not found.");
            }

            var passwordVerification = VerifyHashedPassword(user, userRequest.Password);
            if(passwordVerification == PasswordVerificationResult.Failed)
            {
                throw new InvalidDataException("Incorrect password.");
            }

            string jwt = GenerateJwtToken(user);
            RefreshToken refreshToken = await GenerateRefreshToken(user);

            LoginResponse loginResponse = new LoginResponse
            {
                JWT = jwt,
                RefreshToken = refreshToken.Token,
                RefreshTokenExpiresAt = refreshToken.ExpiresAt
            };

            return loginResponse;
        }

        private async Task<RefreshToken> GenerateRefreshToken(User user)
        {
            var refreshToken = await _context.RefreshToken.FindAsync(user.RefreshTokenId);
            if(refreshToken != null)
            {
                _context.RefreshToken.Remove(refreshToken);
            }

            var newRefreshToken = new RefreshToken
            {
                Token = Guid.NewGuid().ToString(),
                ExpiresAt = DateTime.UtcNow.AddHours(5)
            };

            user.RefreshToken = newRefreshToken;
            _context.User.Update(user);
            _context.SaveChanges();
            
            return newRefreshToken;
        }

        private string GenerateJwtToken(User user)
        {
            var tokenHandler = new JwtSecurityTokenHandler();
            var key = Encoding.ASCII.GetBytes(_configuration.GetSection("JWT:Key").Value);
            var tokenDescriptor = new SecurityTokenDescriptor
            {
                Subject = new ClaimsIdentity(new Claim[]
                {
                    new Claim("id",user.Id.ToString()),
                    new Claim("firstName",user.FirstName),
                    new Claim(ClaimTypes.Email,user.Email),
                    new Claim("roleId",user.RoleId.ToString()),
                    new Claim(ClaimTypes.Role,user.Role.Name)

                }),
                Expires = DateTime.UtcNow.AddMinutes(5),
                SigningCredentials = new SigningCredentials(new SymmetricSecurityKey(key),
                               SecurityAlgorithms.HmacSha256Signature),
                Audience = _configuration.GetSection("JWT:Audience").Value,
                Issuer = _configuration.GetSection("JWT:Issuer").Value
            };
            var token = tokenHandler.CreateToken(tokenDescriptor);
            return tokenHandler.WriteToken(token);
        }

        public async Task<LoginResponse> TokenRefreshAsync(TokenRefreshRequest tokenRefreshRequest)
        {
            var handler = new JwtSecurityTokenHandler();
            var token = handler.ReadJwtToken(tokenRefreshRequest.JWT);
            var userId = token.Claims.First(claim => claim.Type == "id").Value;
            var user = _context.User.Include(r => r.Role).Include(t => t.RefreshToken).FirstOrDefault(u => u.Id == int.Parse(userId));
            
            if(tokenRefreshRequest.RefreshToken != user.RefreshToken.Token || user.RefreshToken.ExpiresAt < DateTime.UtcNow)
            {
                throw new Exception("Invalid refresh token.");
            }

            string jwt = GenerateJwtToken(user);
            RefreshToken refreshToken = await GenerateRefreshToken(user);

            LoginResponse loginResponse = new LoginResponse
            {
                JWT = jwt,
                RefreshToken = refreshToken.Token,
                RefreshTokenExpiresAt = refreshToken.ExpiresAt
            };

            return loginResponse;
        }
    }
}
