using backend.Data;
using backend.Models.Entities;
using backend.Models.Requests;
using backend.Services;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ControllerUnitTests
{
    [TestClass]
    public class CardServiceTests
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
            using (var dbContext = new DatabaseContext(_options))
            {
                dbContext.Database.EnsureDeleted();
            }
        }


        [TestMethod]
        public async Task CreateCardAsync_ValidCard_ReturnsCard()
        {
            // Arrange
            using (var dbContext = new DatabaseContext(_options))
            {
                var user = new User { Id = 1 };
                var card = new CardCreateRequest { Value = "123456" };

                // Act
                var cardService = new CardService(dbContext);
                var result = await cardService.CreateCardAsync(card, user);

                // Assert
                Assert.IsNotNull(result);
                Assert.AreEqual(card.Value, result.Value);
                Assert.AreEqual(user.Id, result.OwnedBy.Id);
                Assert.AreEqual(true, result.Active);
            }
        }

        [TestMethod]
        public async Task CreateCardAsync_CardAlreadyExists_ReturnsNull()
        {
            // Arrange
            using (var dbContext = new DatabaseContext(_options))
            {
                var existingCard = new Card { Value = "123456" };
                dbContext.Card.Add(existingCard);
                dbContext.SaveChanges();

                var user = new User { Id = 1 };
                var card = new CardCreateRequest { Value = "123456" };

                // Act
                var cardService = new CardService(dbContext);
                var result = await cardService.CreateCardAsync(card, user);

                // Assert
                Assert.IsNull(result);
            }
        }
    }
}
