using backend.Data;
using backend.Models.Entities;
using backend.Models.Requests;
using backend.Services;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ControllerUnitTests.ServicesTests
{
    [TestClass]
    public class UserServiceTests
    {
        private DbContextOptions<DatabaseContext> _options;

        [TestInitialize]
        public void TestInitialize()
        {
            _options = new DbContextOptionsBuilder<DatabaseContext>()
                .UseInMemoryDatabase(databaseName: "UserServiceTestsDatabase")
                .Options;
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
                var userService = new UserService(dbContext);
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
                var userService = new UserService(dbContext);
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
                var userService = new UserService(dbContext);
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
                var userService = new UserService(dbContext);
                var result1 = await userService.CreateUserAsync(userRequest1);
                var result2 = await userService.CreateUserAsync(userRequest2);

                // Assert
                Assert.AreNotEqual(result1.Password, result2.Password);
            }
        }
    }
}
