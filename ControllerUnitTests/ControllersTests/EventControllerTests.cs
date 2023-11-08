using backend.Controllers;
using backend.Data;
using backend.Models.Entities;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace ControllerUnitTests.ControllersTests
{
    [TestClass]
    public class EventControllerTests
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
        public async Task GetEvents_WhenThereAreNoEvents_ShouldReturnEmptyList()
        {
            // Arrange
            using var context = new DatabaseContext(_options);
            EventController eventController = new(context, new HttpClient());

            // Act
            var result = (await eventController.GetEvents()).Result as ObjectResult;

            // Assert
            Assert.IsFalse((result.Value as List<Event>).Any());
        }

        [TestMethod]
        public async Task GetEvents_WhenThereAreEvents_ShouldReturnAllEvents()
        {
            // Arrange
            using var context = new DatabaseContext(_options);
            EventController eventController = new(context, new HttpClient());
            context.Event.Add(new Event { Id = 1, CardId = 1, ChargerId = 1, StartedAt = DateTime.Now, EndedAt = DateTime.MaxValue });
            context.Event.Add(new Event { Id = 2, CardId = 2, ChargerId = 2, StartedAt = DateTime.Now, EndedAt = DateTime.MaxValue });
            context.SaveChanges();

            // Act
            var result = (await eventController.GetEvents()).Result as ObjectResult;

            // Assert
            Assert.AreEqual(2, (result.Value as List<Event>).Count);
        }

        [TestMethod]
        public async Task GetEventsForCard_WhenCardDoesNotExist_ShouldReturnNotFoundException()
        {
            // Arrange
            using var context = new DatabaseContext(_options);
            EventController eventController = new(context, new HttpClient());
            context.Event.Add(new Event { Id = 1, CardId = 1, ChargerId = 1, StartedAt = DateTime.Now, EndedAt = DateTime.MaxValue });
            context.Event.Add(new Event { Id = 2, CardId = 2, ChargerId = 2, StartedAt = DateTime.Now, EndedAt = DateTime.MaxValue });
            context.SaveChanges();

            // Act
            var result = await eventController.GetEventsForCard(1);

            // Assert
            Assert.IsInstanceOfType(result.Result, typeof(NotFoundObjectResult));
        }
    }
}