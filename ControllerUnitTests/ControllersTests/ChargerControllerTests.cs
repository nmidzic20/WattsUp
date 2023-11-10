using backend.Controllers;
using backend.Data;
using backend.Models.Entities;
using backend.Models.Requests;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace ControllerUnitTests.ControllersTests
{
    [TestClass]
    public class ChargerControllerTests
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
        public async Task GetChargers_WhenThereAreNoChargers_ShouldReturnEmptyList()
        {
            // Arrange
            using var context = new DatabaseContext(_options);
            ChargerController chargerController = new(context, new HttpClient());

            // Act
            var result = (await chargerController.GetChargers()).Result as ObjectResult;

            // Assert
            Assert.IsFalse((result.Value as List<Charger>).Any());
        }

        [TestMethod]
        public async Task GetChargers_WhenThereAreChargers_ShouldReturnAllChargers()
        {
            // Arrange
            using var context = new DatabaseContext(_options);
            ChargerController chargerController = new(context, new HttpClient());
            context.Charger.Add(new Charger
            {
                Id = 1,
                CreatedAt = DateTime.Now,
                Name = "Test Charger 1",
                Active = true,
                Latitude = 1.1,
                Longitude = 1.1
            });
            context.Charger.Add(new Charger
            {
                Id = 2,
                CreatedAt = DateTime.Now,
                Name = "Test Charger 2",
                Active = true,
                Latitude = 1.1,
                Longitude = 1.1
            });
            context.SaveChanges();

            // Act
            var result = (await chargerController.GetChargers()).Result as ObjectResult;

            // Assert
            Assert.AreEqual(2, (result.Value as List<Charger>).Count);
        }

        [TestMethod]
        public async Task GetChargerByID_WhenThereIsNoChargerWithGivenID_ShouldReturnError404()
        {
            // Arrange
            using var context = new DatabaseContext(_options);
            ChargerController chargerController = new(context, new HttpClient());
            context.Charger.Add(new Charger
            {
                Id = 1,
                CreatedAt = DateTime.Now,
                Name = "Test Charger 1",
                Active = true,
                Latitude = 1.1,
                Longitude = 1.1
            });
            context.SaveChanges();

            // Act
            var chargers = await chargerController.GetChargerByID(9999);
            var result = chargers.Result as StatusCodeResult;

            // Assert
            Assert.AreEqual(404, result.StatusCode);
        }

        [TestMethod]
        public async Task GetChargerByID_WhenThereIsChargerWithGivenID_ShouldReturnThatCharger()
        {
            // Arrange
            using var context = new DatabaseContext(_options);
            ChargerController chargerController = new(context, new HttpClient());
            context.Charger.Add(new Charger
            {
                Id = 99,
                CreatedAt = DateTime.Now,
                Name = "Test Charger 1",
                Active = true,
                Latitude = 1.1,
                Longitude = 1.1
            });
            context.SaveChanges();

            // Act
            var result = (await chargerController.GetChargerByID(99)).Result as ObjectResult;

            // Assert
            Assert.AreEqual(99, (result.Value as Charger).Id);
        }

        [TestMethod]
        public async Task GetChargersByState_Active_WhenThereAreActiveChargers_ShouldReturnActiveChargers()
        {
            // Arrange
            using var context = new DatabaseContext(_options);
            ChargerController chargerController = new(context, new HttpClient());
            context.Charger.Add(new Charger
            {
                Id = 1,
                CreatedAt = DateTime.Now,
                Name = "Test Charger 1",
                Active = true,
                Latitude = 1.1,
                Longitude = 1.1
            });
            context.Charger.Add(new Charger
            {
                Id = 2,
                CreatedAt = DateTime.Now,
                Name = "Test Charger 2",
                Active = false,
                Latitude = 1.1,
                Longitude = 1.1
            });
            context.Charger.Add(new Charger
            {
                Id = 3,
                CreatedAt = DateTime.Now,
                Name = "Test Charger 3",
                Active = true,
                Latitude = 1.1,
                Longitude = 1.1
            });
            context.SaveChanges();

            // Act
            var result = (await chargerController.GetChargersByState(true)).Result as ObjectResult;

            // Assert
            Assert.AreEqual("Test Charger 1", (result.Value as List<Charger>).FirstOrDefault().Name);
            Assert.AreEqual("Test Charger 3", (result.Value as List<Charger>).Last().Name);
        }

        [TestMethod]
        public async Task GetChargersByState_NotActive_WhenThereAreNonActiveChargers_ShouldReturnNonActiveChargers()
        {
            // Arrange
            using var context = new DatabaseContext(_options);
            ChargerController chargerController = new(context, new HttpClient());
            context.Charger.Add(new Charger
            {
                Id = 1,
                CreatedAt = DateTime.Now,
                Name = "Test Charger 1",
                Active = false,
                Latitude = 1.1,
                Longitude = 1.1
            });
            context.Charger.Add(new Charger
            {
                Id = 2,
                CreatedAt = DateTime.Now,
                Name = "Test Charger 2",
                Active = true,
                Latitude = 1.1,
                Longitude = 1.1
            });
            context.Charger.Add(new Charger
            {
                Id = 3,
                CreatedAt = DateTime.Now,
                Name = "Test Charger 3",
                Active = false,
                Latitude = 1.1,
                Longitude = 1.1
            });
            context.SaveChanges();

            // Act
            var result = (await chargerController.GetChargersByState(false)).Result as ObjectResult;

            // Assert
            Assert.AreEqual("Test Charger 1", (result.Value as List<Charger>).FirstOrDefault().Name);
            Assert.AreEqual("Test Charger 3", (result.Value as List<Charger>).Last().Name);
        }

        [TestMethod]
        public async Task AddCharger_GivenValidData_ShouldReturnSuccess()
        {
            // Arrange
            using var context = new DatabaseContext(_options);
            ChargerController chargerController = new(context, new HttpClient());
            context.User.Add(new User
            {
                Id = 1,
                Email = "test",
                FirstName = "test",
                LastName = "test",
                Password = "test"
            });
            context.SaveChanges();

            // Act
            await chargerController.AddCharger(new ChargerCreateRequest
            {
                Latitude = 1.0,
                Longitude = 1.0,
                Name = "Test Charger 1",
                CreatedById = 1
            });

            // Assert
            Assert.AreEqual(1, context.Charger.Count());
        }

        [TestMethod]
        public async Task UpdateCharger_GivenChargerDoesntExist_ShouldReturnError404()
        {
            // Arrange
            using var context = new DatabaseContext(_options);
            ChargerController chargerController = new(context, new HttpClient());

            // Act
            var chargers = await chargerController.UpdateChargerByID(9999, new ChargerCreateRequest
            {
                Active = false,
                Latitude = 1.0,
                Longitude = 1.0,
                Name = "Test Charger 1",
                CreatedById = 1
            });

            var result = chargers.Result as StatusCodeResult;

            // Assert
            Assert.AreEqual(404, result.StatusCode);
        }

        [TestMethod]
        public async Task UpdateCharger_GivenChargerExists_ShouldReturnUpdatedCharger()
        {
            // Arrange
            using var context = new DatabaseContext(_options);
            ChargerController chargerController = new(context, new HttpClient());
            context.Charger.Add(new Charger
            {
                Id = 1,
                CreatedAt = DateTime.Now,
                Name = "Test Charger 3",
                Active = false,
                Latitude = 1.1,
                Longitude = 1.1
            });
            context.SaveChanges();

            // Act
            await chargerController.UpdateChargerByID(1, new ChargerCreateRequest
            {
                Active = false,
                Latitude = 1.0,
                Longitude = 1.0,
                Name = "TESTNOW",
                CreatedById = 1
            });

            // Assert
            Assert.AreEqual("TESTNOW", context.Charger.FirstOrDefault().Name);
        }

        [TestMethod]
        public async Task DeleteCharger_GivenChargerDoesntExist_ShouldReturnError404()
        {
            // Arrange
            using var context = new DatabaseContext(_options);
            ChargerController chargerController = new(context, new HttpClient());

            // Act
            var chargers = await chargerController.DeleteChargerByID(9999);
            var result = chargers.Result as StatusCodeResult;

            // Assert
            Assert.AreEqual(404, result.StatusCode);
        }

        [TestMethod]
        public async Task DeleteCharger_GivenChargerExists_ShouldReturnDeletedCharger()
        {
            // Arrange
            using var context = new DatabaseContext(_options);
            ChargerController chargerController = new(context, new HttpClient());
            context.Charger.Add(new Charger
            {
                Id = 1,
                CreatedAt = DateTime.Now,
                Name = "Test Charger 3",
                Active = false,
                Latitude = 1.1,
                Longitude = 1.1
            });
            context.Charger.Add(new Charger
            {
                Id = 2,
                CreatedAt = DateTime.Now,
                Name = "Test Charger 3",
                Active = false,
                Latitude = 1.1,
                Longitude = 1.1
            });
            context.SaveChanges();

            // Act
            await chargerController.DeleteChargerByID(1);

            // Assert
            Assert.AreEqual(1, context.Charger.Count());
            Assert.AreEqual(2, context.Charger.FirstOrDefault().Id);
        }
    }
}