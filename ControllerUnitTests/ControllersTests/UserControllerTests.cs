using backend.Controllers;
using backend.Data;
using backend.Models.Entities;
using backend.Models.Requests;
using backend.Services;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ControllerUnitTests.ControllersTests
{
    [TestClass]
    public class UserControllerTests
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
        public async Task PostUser_ValidUserWithCard_ReturnsOk()
        {
            // Arrange
            using (var dbContext = new DatabaseContext(_options))
            { 
                var user = new UserCreateRequest
                {
                    FirstName = "John",
                    LastName = "Doe",
                    Email = "john.doe@example.com",
                    Password = "123456",
                    Card = new CardCreateRequest { Value = "123456" }
                };
                
                var userService = new UserService(dbContext, _configuration);
                var cardService = new CardService(dbContext);

                // Act
                var controller = new UsersController(dbContext, userService, cardService);
                var result = await controller.PostUser(user);

                // Assert
                Assert.IsInstanceOfType(result.Result, typeof(OkObjectResult));
            }
        }

        [TestMethod]
        public async Task PostUser_ValidUserWithoutCard_ReturnsOk()
        {
            // Arrange
            using (var dbContext = new DatabaseContext(_options))
            {
                var user = new UserCreateRequest
                {
                    FirstName = "John",
                    LastName = "Doe",
                    Email = "john.doe@example.com",
                    Password = "123456"
                };

                var userService = new UserService(dbContext, _configuration);
                var cardService = new CardService(dbContext);

                // Act
                var controller = new UsersController(dbContext, userService, cardService);
                var result = await controller.PostUser(user);

                // Assert
                Assert.IsInstanceOfType(result.Result, typeof(OkObjectResult));
            }
        }

        [TestMethod]
        public async Task PostUser_UserAlreadyExists_ReturnsConfllict()
        {
            // Arrange
            using (var dbContext = new DatabaseContext(_options))
            {
                var existingUser = new User
                {
                    FirstName = "John",
                    LastName = "Doe",
                    Email = "john.doe@example.com",
                    Password = "123456"
                };
                dbContext.User.Add(existingUser);
                dbContext.SaveChanges();

                var user = new UserCreateRequest
                {
                    Email = "john.doe@example.com"
                };

                var userService = new UserService(dbContext, _configuration);
                var cardService = new CardService(dbContext);

                // Act
                var controller = new UsersController(dbContext, userService, cardService);
                var result = await controller.PostUser(user);

                // Assert
                Assert.IsInstanceOfType(result.Result, typeof(ConflictObjectResult));
            }
        }

        [TestMethod]
        public async Task PostUser_CardAlreadyExists_ReturnsConfllict()
        {
            // Arrange
            using (var dbContext = new DatabaseContext(_options))
            {
                var existingCard = new Card { Value = "123456" };
                dbContext.Card.Add(existingCard);
                dbContext.SaveChanges();

                var user = new UserCreateRequest
                {
                    FirstName = "John",
                    LastName = "Doe",
                    Email = "john.doe@example.com",
                    Password = "123456",
                    Card = new CardCreateRequest { Value = "123456" }
                };

                var userService = new UserService(dbContext, _configuration);
                var cardService = new CardService(dbContext);

                // Act
                var controller = new UsersController(dbContext, userService, cardService);
                var result = await controller.PostUser(user);

                // Assert
                Assert.IsInstanceOfType(result.Result, typeof(ConflictObjectResult));
            }
        }

        [TestMethod]
        public async Task Login_ValidUser_ReturnsOk()
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
                var controller = new UsersController(dbContext, userService, cardService: null);
                var result = await controller.Login(userLoginRequest);

                // Assert
                Assert.IsInstanceOfType(result.Result, typeof(OkObjectResult));
            }
        }

        [TestMethod]
        public async Task Login_IncorrectPassword_ReturnsUnauthorized()
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
                var controller = new UsersController(dbContext, userService, cardService: null);
                var result = await controller.Login(userLoginRequest);

                // Assert
                Assert.IsInstanceOfType(result.Result, typeof(UnauthorizedObjectResult));
            }
        }

        [TestMethod]
        public async Task Login_UserNotFound_ReturnsNotFound()
        {
            // Arrange
            using (var dbContext = new DatabaseContext(_options))
            {
                var userRequest = new UserLoginRequest
                {
                    Email = "nonexistent@example.com",
                    Password = "password"
                };

                var userService = new UserService(dbContext, _configuration);

                // Act
                var controller = new UsersController(dbContext, userService, cardService: null);
                var result = await controller.Login(userRequest);

                // Assert
                Assert.IsInstanceOfType(result.Result, typeof(NotFoundObjectResult));
            }
        }

        [TestMethod]
        public async Task TokenRefresh_ValidRequest_ReturnsOk()
        {
            // Arrange
            using (var dbContext = new DatabaseContext(_options))
            {
                var user = new User
                {
                    Id = 1,
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
                        Token = "valid_token",
                        ExpiresAt = DateTime.UtcNow.AddMinutes(1)
                    }
                };

                dbContext.User.Add(user);
                dbContext.SaveChanges();

                var userService = new UserService(dbContext, _configuration);
                var tokenRefreshRequest = new TokenRefreshRequest
                {
                    JWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjEiLCJmaXJzdE5hbWUiOiJKb2huIiwiZW1haWwiOiJqb2huLmRvZUBnbWFpbC5jb20iLCJyb2xlSWQiOiIyIiwicm9sZSI6IlVzZXIiLCJuYmYiOjE2OTk2MjI4NTksImV4cCI6MTY5OTYyMzE1OCwiaWF0IjoxNjk5NjIyODU5LCJpc3MiOiJOdWxsUG9pbnRlci5jb20iLCJhdWQiOiJOdWxsUG9pbnRlci5jb20ifQ._-RPcRIfh0XeUzpEIqm0Vgl-_yrjhqFSD-9aRfmm4KE",
                    RefreshToken = "valid_token"
                };

                // Act
                var controller = new UsersController(dbContext, userService, cardService: null);
                var result = await controller.TokenRefresh(tokenRefreshRequest);

                // Assert
                Assert.IsInstanceOfType(result.Result, typeof(OkObjectResult));
            }
        }

        [TestMethod]
        public async Task TokenRefresh_InvalidRefreshToken_ReturnsUnauthorized()
        {
            // Arrange
            using (var dbContext = new DatabaseContext(_options))
            {
                var user = new User
                {
                    Id = 1,
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
                        Token = "invalid_token",
                        ExpiresAt = DateTime.UtcNow
                    }
                };

                dbContext.User.Add(user);
                dbContext.SaveChanges();

                var userService = new UserService(dbContext, _configuration);
                var tokenRefreshRequest = new TokenRefreshRequest
                {
                    JWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjEiLCJmaXJzdE5hbWUiOiJKb2huIiwiZW1haWwiOiJqb2huLmRvZUBnbWFpbC5jb20iLCJyb2xlSWQiOiIyIiwicm9sZSI6IlVzZXIiLCJuYmYiOjE2OTk2MjI4NTksImV4cCI6MTY5OTYyMzE1OCwiaWF0IjoxNjk5NjIyODU5LCJpc3MiOiJOdWxsUG9pbnRlci5jb20iLCJhdWQiOiJOdWxsUG9pbnRlci5jb20ifQ._-RPcRIfh0XeUzpEIqm0Vgl-_yrjhqFSD-9aRfmm4KE",
                    RefreshToken = "valid_token"
                };

                // Act
                var controller = new UsersController(dbContext, userService, cardService: null);
                var result = await controller.TokenRefresh(tokenRefreshRequest);

                // Assert
                Assert.IsInstanceOfType(result.Result, typeof(UnauthorizedObjectResult));
            }
        }
    }
}
