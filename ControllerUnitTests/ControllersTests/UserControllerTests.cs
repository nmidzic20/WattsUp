using backend.Controllers;
using backend.Data;
using backend.Models.Entities;
using backend.Models.Requests;
using backend.Services;
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

            _configuration = new ConfigurationBuilder().AddInMemoryCollection(new Dictionary<string, string>()).Build();
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
    }
}
