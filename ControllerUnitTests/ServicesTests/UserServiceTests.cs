using Azure;
using backend.Data;
using backend.Models.Entities;
using backend.Models.Requests;
using backend.Models.Responses;
using backend.Services;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.IdentityModel.Tokens;
using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Security.Claims;
using System.Text;
using System.Threading.Tasks;

namespace ControllerUnitTests.ServicesTests
{
    [TestClass]
    public class UserServiceTests
    {
        private DbContextOptions<DatabaseContext> _options;
        private IConfiguration _configuration;

        [TestInitialize]
        public void TestInitialize()
        {
            _options = new DbContextOptionsBuilder<DatabaseContext>()
                .UseInMemoryDatabase(databaseName: "WattsUpDatabase")
                .Options;

            _configuration = new ConfigurationBuilder()
                .SetBasePath(AppContext.BaseDirectory)
                .AddJsonFile("appsettings.json", optional: false, reloadOnChange: true)
                .Build();
        }

        [TestCleanup]
        public void TestCleanup()
        {
            using (var dbContext = new DatabaseContext(_options))
            {
                dbContext.Database.EnsureDeleted();
            }
        }


        [TestMethod]
        public async Task CreateUserAsync_ValidUser_ReturnsUser()
        {
            // Arrange
            using (var dbContext = new DatabaseContext(_options))
            {
                var userRequest = new UserCreateRequest
                {
                    FirstName = "John",
                    LastName = "Doe",
                    Email = "john.doe@gmail.com",
                    Password = "123456"
                };

                // Act
                var userService = new UserService(dbContext, _configuration);
                var result = await userService.CreateUserAsync(userRequest);

                // Assert
                Assert.IsNotNull(result);
                Assert.AreEqual(userRequest.FirstName, result.FirstName);
                Assert.AreEqual(userRequest.LastName, result.LastName);
                Assert.AreEqual(userRequest.Email, result.Email);
                Assert.AreEqual(true, result.Active);
                Assert.IsTrue((result.CreatedAt - DateTime.UtcNow).Duration() <= TimeSpan.FromMinutes(2));
                Assert.AreEqual(2, result.RoleId);
            }
        }

        [TestMethod]
        public async Task CreateUserAsync_UserAlreadyExists_ReturnsNull()
        {
            // Arrange
            using (var dbContext = new DatabaseContext(_options))
            {
                var existingUser = new User
                {
                    FirstName = "John",
                    LastName = "Doe",
                    Email = "john.doe@gmail.com",
                    Password = "123456"
                };

                dbContext.User.Add(existingUser);
                dbContext.SaveChanges();

                var userRequest = new UserCreateRequest
                {
                    Email = "john.doe@gmail.com",
                };

                // Act
                var userService = new UserService(dbContext, _configuration);
                var result = await userService.CreateUserAsync(userRequest);

                // Assert
                Assert.IsNull(result);
            }
        }

        [TestMethod]
        public async Task CreateUserAsync_ValidUser_PasswordHashedAndValid()
        {
            // Arrange
            using (var dbContext = new DatabaseContext(_options))
            {
                var userRequest = new UserCreateRequest
                {
                    FirstName = "John",
                    LastName = "Doe",
                    Email = "john.doe@gmail.com",
                    Password = "123456"
                };

                // Act
                var userService = new UserService(dbContext, _configuration);
                var result = await userService.CreateUserAsync(userRequest);

                // Assert
                Assert.IsNotNull(result);
                var passwordHasher = new PasswordHasher<User>();
                var isPasswordValid = passwordHasher.VerifyHashedPassword(result, result.Password, userRequest.Password);
                Assert.AreEqual(PasswordVerificationResult.Success, isPasswordValid);
            }
        }

        [TestMethod]
        public async Task CreateUserAsync_DifferentUsersWithSamePasswords_DifferentHashedPasswords()
        {
            // Arrange
            using (var dbContext = new DatabaseContext(_options))
            {
                var userRequest1 = new UserCreateRequest
                {
                    FirstName = "John",
                    LastName = "Doe",
                    Email = "john.doe@gmail.com",
                    Password = "123456"
                };
                var userRequest2 = new UserCreateRequest
                {
                    FirstName = "John",
                    LastName = "Doe",
                    Email = "djohn@gmail.com",
                    Password = "123456"
                };

                // Act
                var userService = new UserService(dbContext, _configuration);
                var result1 = await userService.CreateUserAsync(userRequest1);
                var result2 = await userService.CreateUserAsync(userRequest2);

                // Assert
                Assert.AreNotEqual(result1.Password, result2.Password);
            }
        }

        [TestMethod]
        [ExpectedException(typeof(Exception), "User not found.")]
        public async Task LoginAsync_UserNotFound_ThrowsException()
        {
            // Arrange
            using (var dbContext = new DatabaseContext(_options))
            {
                var userService = new UserService(dbContext, _configuration);
                var userLoginRequest = new UserLoginRequest
                {
                    Email = "john.doe@gmail.com",
                    Password = "123456"
                };

                // Act
                await userService.LoginAsync(userLoginRequest);
            }
        }

        [TestMethod]
        [ExpectedException(typeof(InvalidDataException), "Incorrect password.")]
        public async Task LoginAsync_IncorrectPassword_ThrowsException()
        {
            // Arrange
            using (var dbContext = new DatabaseContext(_options))
            {
                var user = new User
                {
                    FirstName = "John",
                    LastName = "Doe",
                    Email = "john.doe@gmail.com",
                    Password = "AQAAAAIAAYagAAAAEE0LVQ5mGf8bCCIJ/r3vv4TD3w+MkMgw+wXZIYveI7zwcBJWWdFs9AmwNwOlHpcfNw==",
                    Active = true,
                    CreatedAt = DateTime.UtcNow,
                    Role = new Role
                    {
                        Id = 2,
                        Name = "User"
                    },
                    RefreshToken = new RefreshToken
                    {
                        Id = 2,
                        Token = "token",
                        ExpiresAt = DateTime.UtcNow.AddHours(5),
                    }

                };
                dbContext.User.Add(user);
                dbContext.SaveChanges();

                var userService = new UserService(dbContext, _configuration);
                var userLoginRequest = new UserLoginRequest
                {
                    Email = "john.doe@gmail.com",
                    Password = "password123456"
                };

                // Act
                await userService.LoginAsync(userLoginRequest);
            }
        }

        private async Task<(User, LoginResponse)> ValidUserArrange()
        {
            // Arrange
            using (var dbContext = new DatabaseContext(_options))
            {
                var user = new User
                {
                    FirstName = "John",
                    LastName = "Doe",
                    Email = "john.doe@gmail.com",
                    Password = "123456",
                    Active = true,
                    CreatedAt = DateTime.UtcNow,
                    Role = new Role
                    {
                        Id = 2,
                        Name = "User"
                    },
                    RefreshToken = new RefreshToken
                    {
                        Id = 1,
                        Token = "token",
                        ExpiresAt = DateTime.UtcNow.AddHours(5)
                    }

                };
                var passwordHasher = new PasswordHasher<User>();
                string hashedPassword = passwordHasher.HashPassword(user, user.Password);
                user.Password = hashedPassword;
                dbContext.User.Add(user);
                dbContext.SaveChanges();

                var userService = new UserService(dbContext, _configuration);
                var userLoginRequest = new UserLoginRequest
                {
                    Email = "john.doe@gmail.com",
                    Password = "123456"
                };

                // Act
                var response = await userService.LoginAsync(userLoginRequest);

                return (user, response);
            }
        }

        [TestMethod]
        public async Task LoginAsync_ValidUser_ReturnsValidJWT()
        {
            var (user, response) = await ValidUserArrange();
            // Assert
            Assert.IsNotNull(response.JWT);

            var handler = new JwtSecurityTokenHandler();
            var token = handler.ReadJwtToken(response.JWT);

            var id = token.Claims.First(claim => claim.Type == "id").Value;
            var name = token.Claims.First(claim => claim.Type == "firstName").Value;
            var email = token.Claims.First(claim => claim.Type == "email").Value;
            var roleId = token.Claims.First(claim => claim.Type == "roleId").Value;
            var roleName = token.Claims.First(claim => claim.Type == "role").Value;

            Assert.AreEqual(user.Id.ToString(), id);
            Assert.AreEqual(user.FirstName, name);
            Assert.AreEqual(user.Email, email);
            Assert.AreEqual(user.RoleId.ToString(), roleId);
            Assert.AreEqual(user.Role.Name, roleName);
            Assert.IsTrue(token.ValidTo < DateTime.UtcNow.AddMinutes(5));
            Assert.IsTrue(token.ValidTo > DateTime.UtcNow.AddMinutes(4));
        }

        [TestMethod]
        public async Task LoginAsync_ValidUser_ReturnsValidRefreshToken()
        {
            var (_, response) = await ValidUserArrange();
            // Assert
            Assert.IsNotNull(response.RefreshToken);
            Assert.AreNotEqual("token", response.RefreshToken);
            Assert.IsTrue(response.RefreshTokenExpiresAt > DateTime.UtcNow.AddHours(4).AddMinutes(59));
            Assert.IsTrue(response.RefreshTokenExpiresAt < DateTime.UtcNow.AddHours(5).AddMinutes(1));
        }
    }
}
