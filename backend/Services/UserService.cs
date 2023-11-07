using backend.Data;
using backend.Models.Entities;
using backend.Models.Requests;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;

namespace backend.Services
{
    public class UserService
    {
        private readonly DatabaseContext _context;

        public UserService(DatabaseContext context)
        {
            _context = context;
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

        public async Task<User> LoginAsync(UserLoginRequest userRequest)
        {
            throw new NotImplementedException();
        }
    }
}
