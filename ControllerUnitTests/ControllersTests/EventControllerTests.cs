using backend.Controllers;
using backend.Data;
using backend.Models.Entities;
using backend.Models.Requests;
using backend.Services;
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
            EventController eventController = new(context, new HttpClient(),new SSEService());

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
            EventController eventController = new(context, new HttpClient(), new SSEService());
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
            EventController eventController = new(context, new HttpClient(), new SSEService());
            context.Event.Add(new Event { Id = 1, CardId = 1, ChargerId = 1, StartedAt = DateTime.Now, EndedAt = DateTime.MaxValue });
            context.Event.Add(new Event { Id = 2, CardId = 2, ChargerId = 2, StartedAt = DateTime.Now, EndedAt = DateTime.MaxValue });
            context.SaveChanges();

            // Act
            var result = await eventController.GetEventsForCard(1);

            // Assert
            Assert.IsInstanceOfType(result.Result, typeof(NotFoundObjectResult));
        }

        [TestMethod]
        public async Task GetEventsForCard_WhenCardExists_ShouldReturnEventsForCard()
        {
            // Arrange
            using var context = new DatabaseContext(_options);
            EventController eventController = new(context, new HttpClient(), new SSEService());
            context.Card.Add(new Card
            {
                Id = 1,
                Active = true,
                Events = new List<Event> { },
                OwnedBy = new User 
                { 
                    Email = "test",
                    FirstName = "test",
                    LastName = "test",
                    Password = "test"
                },
                OwnedById = 1,
                Value = "1"
            });
            context.Event.Add(new Event { Id = 1, CardId = 1, ChargerId = 1, StartedAt = DateTime.Now, EndedAt = DateTime.MaxValue });
            context.Event.Add(new Event { Id = 2, CardId = 2, ChargerId = 2, StartedAt = DateTime.Now, EndedAt = DateTime.MaxValue });
            context.SaveChanges();

            // Act
            var result = await eventController.GetEventsForCard(1);

            // Assert
            Assert.AreEqual(1, ((result.Result as ObjectResult).Value as List<Event>).Count);
        }

        [TestMethod]
        public async Task GetEventsForCharger_WhenChargerDoesNotExist_ShouldReturnNotFoundException()
        {
            // Arrange
            using var context = new DatabaseContext(_options);
            EventController eventController = new(context, new HttpClient(), new SSEService());
            context.Event.Add(new Event { Id = 1, CardId = 1, ChargerId = 1, StartedAt = DateTime.Now, EndedAt = DateTime.MaxValue });
            context.Event.Add(new Event { Id = 2, CardId = 2, ChargerId = 2, StartedAt = DateTime.Now, EndedAt = DateTime.MaxValue });
            context.SaveChanges();

            // Act
            var result = await eventController.GetEventsForCharger(1);

            // Assert
            Assert.IsInstanceOfType(result.Result, typeof(NotFoundObjectResult));
        }

        [TestMethod]
        public async Task GetEventsForCharger_WhenChargerExists_ShouldReturnEventsForCard()
        {
            // Arrange
            using var context = new DatabaseContext(_options);
            EventController eventController = new(context, new HttpClient(), new SSEService());
            context.Charger.Add(new Charger
            {
                Active = true,
                Events = new List<Event> { },
                Id = 1,
                Name = "test",
                CreatedAt = DateTime.Now,
                CreatedBy = new User
                {
                    Email = "test",
                    FirstName = "test",
                    LastName = "test",
                    Password = "test"
                },
                CreatedById = 1
            });
            context.Event.Add(new Event { Id = 1, CardId = 1, ChargerId = 1, StartedAt = DateTime.Now, EndedAt = DateTime.MaxValue });
            context.Event.Add(new Event { Id = 2, CardId = 2, ChargerId = 2, StartedAt = DateTime.Now, EndedAt = DateTime.MaxValue });
            context.SaveChanges();

            // Act
            var result = await eventController.GetEventsForCharger(1);

            // Assert
            Assert.AreEqual(1, ((result.Result as ObjectResult).Value as List<Event>).Count);
        }

        [TestMethod]
        public async Task GetCurrentEvents_WhenThereAreOngoingEvents_ShouldReturnOngoingEvents()
        {
            // Arrange
            using var context = new DatabaseContext(_options);
            EventController eventController = new(context, new HttpClient(), new SSEService());
            context.Event.Add(new Event { Id = 1, CardId = 1, ChargerId = 1, StartedAt = DateTime.Today, EndedAt = DateTime.Now });
            context.Event.Add(new Event { Id = 2, CardId = 2, ChargerId = 2, StartedAt = DateTime.Now, EndedAt = DateTime.MaxValue });
            context.SaveChanges();

            // Act
            var result = await eventController.GetCurrentEvents();

            // Assert
            Assert.AreEqual(1, ((result.Result as ObjectResult).Value as List<Event>).Count);
        }

        [TestMethod]
        public async Task CreateEvent_GivenValidData_ShouldReturnSuccess()
        {
            // Arrange
            using var context = new DatabaseContext(_options);
            EventController eventController = new(context, new HttpClient(), new SSEService());
            context.Charger.Add(new Charger
            {
                Active = true,
                Events = new List<Event> { },
                Id = 1,
                Name = "test",
                CreatedAt = DateTime.Now,
                CreatedBy = new User
                {
                    Email = "test",
                    FirstName = "test",
                    LastName = "test",
                    Password = "test"
                },
                CreatedById = 1
            });
            context.Card.Add(new Card
            {
                Id = 1,
                Active = true,
                Events = new List<Event> { },
                OwnedBy = new User
                {
                    Email = "test",
                    FirstName = "test",
                    LastName = "test",
                    Password = "test"
                },
                OwnedById = 1,
                Value = "1"
            });
            context.SaveChanges();

            var newEvent = new EventCreateRequest
            {
                CardId = 1,
                ChargerId = 1,
            };

            // Act
            var result = await eventController.CreateEvent(newEvent);
            
            // Assert
            Assert.AreEqual(1, context.Event.Count());
        }

        [TestMethod]
        public async Task EndEvent_GivenValidData_ShouldReturnSuccess()
        {
            // Arrange
            using var context = new DatabaseContext(_options);
            EventController eventController = new(context, new HttpClient(), new SSEService());
            
            context.Charger.Add(new Charger
            {
                Active = true,
                Events = new List<Event> { },
                Id = 1,
                Name = "test",
                CreatedAt = DateTime.Now,
                CreatedBy = new User
                {
                    Email = "test",
                    FirstName = "test",
                    LastName = "test",
                    Password = "test"
                },
                CreatedById = 1
            });
            context.Card.Add(new Card
            {
                Id = 1,
                Active = true,
                Events = new List<Event> { },
                OwnedBy = new User
                {
                    Email = "test",
                    FirstName = "test",
                    LastName = "test",
                    Password = "test"
                },
                OwnedById = 1,
                Value = "1"
            });
            context.Event.Add(new Event
            {
                Id = 1,
                CardId = 1,
                ChargerId = 1,
                StartedAt = DateTime.Now,
                EndedAt = DateTime.MaxValue
            });
            context.SaveChanges();

            // Act
            var result = await eventController.EndEvent(new EventEndRequest
            {
                Id = 1,
                VolumeKwh = 10.0d
            }
            );

            // Assert
            Assert.IsFalse(context.Charger.Find(1).Active);
            Assert.AreNotEqual(DateTime.MaxValue, context.Event.Find(1).EndedAt);
        }
    }
}