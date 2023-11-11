using backend.Controllers;
using backend.Data;
using backend.Models.Entities;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace ControllerUnitTests.ControllersTests
{
    [TestClass]
    public class CardControllerTests
    {
        private DbContextOptions<DatabaseContext> _options;

        [TestInitialize]
        public void TestInitialize()
        {
            _options = new DbContextOptionsBuilder<DatabaseContext>()
                .UseInMemoryDatabase(databaseName: "WattsUpDatabase")
                .Options;
        }

        [TestCleanup]
        public void TestCleanup()
        {
            using var dbContext = new DatabaseContext(_options);
            dbContext.Database.EnsureDeleted();
        }

        [TestMethod]
        public async Task GetCards_WhenThereAreCards_ShouldReturnAllCards()
        {
            // Arrange
            using var dbContext = new DatabaseContext(_options);
            var card1 = new Card { Id = 1, Value = "123456" };
            var card2 = new Card { Id = 2, Value = "654321" };
            var card3 = new Card { Id = 3, Value = "111111" };
            dbContext.Card.Add(card1);
            dbContext.Card.Add(card2);
            dbContext.Card.Add(card3);
            dbContext.SaveChanges();

            // Act
            var cardController = new CardController(dbContext);
            var result = (await cardController.GetCards()).Result as ObjectResult;

            // Assert
            Assert.IsNotNull(result);
            Assert.AreEqual(3, (result.Value as List<Card>).Count);
        }

        [TestMethod]
        public async Task GetCardsForUser_WhenUserExists_ShouldReturnAllCardsForUser()
        {
            // Arrange
            using var dbContext = new DatabaseContext(_options);
            var user = new User { FirstName = "test", LastName = "test", Password = "test", Active = true, Id = 1, Email = "test@test.com" };
            var card1 = new Card { Id = 1, Value = "123456", OwnedById = 1};
            var card2 = new Card { Id = 2, Value = "654321", OwnedById = 1};
            var card3 = new Card { Id = 3, Value = "111111", OwnedById = 1};
            dbContext.Card.Add(card1);
            dbContext.Card.Add(card2);
            dbContext.Card.Add(card3);
            dbContext.User.Add(user);
            dbContext.SaveChanges();

            // Act
            var cardController = new CardController(dbContext);
            var result = (await cardController.GetCardsForUser(user.Id)).Result as ObjectResult;

            // Assert
            Assert.IsNotNull(result);
            Assert.AreEqual(3, (result.Value as List<Card>).Count);
        }

        [TestMethod]
        public async Task GetCardsForUser_WhenUserDoesNotExist_ShouldReturnNotFound()
        {
            // Arrange
            using var dbContext = new DatabaseContext(_options);

            // Act
            var cardController = new CardController(dbContext);
            var result = await cardController.GetCardsForUser(1);
            
            // Assert
            Assert.IsInstanceOfType(result.Result, typeof(NotFoundObjectResult));
        }
    }
}
