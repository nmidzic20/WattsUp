using backend.Controllers;
using backend.Data;
using backend.Models.Entities;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using System.Threading.Tasks;
using System;

namespace ControllerUnitTests
{
    [TestClass]
    public class ChargerControllerTests
    {
        [TestMethod]
        public async Task GetChargers_WhenThereAreNoChargers_ShouldReturnEmptyList()
        {
            // Arrange
            var options = new DbContextOptionsBuilder<DatabaseContext>()
                .UseInMemoryDatabase(databaseName: "WattsUpDatabase")
                .Options;

            using (var context = new DatabaseContext(options))
            {
                context.Charger.RemoveRange(context.Charger);
                context.SaveChanges();
            }

            using (var context = new DatabaseContext(options))
            {
                ChargerController chargerController = new ChargerController(context, new HttpClient());

                // Act
                var result = (await chargerController.GetChargers()).Result as ObjectResult;

                // Assert
                Assert.IsFalse((result.Value as List<Charger>).Any());
            }
        }

        [TestMethod]
        public async Task GetChargers_WhenThereAreChargers_ShouldReturnAllChargers()
        {
            // Arrange
            var options = new DbContextOptionsBuilder<DatabaseContext>()
                .UseInMemoryDatabase(databaseName: "WattsUpDatabase")
                .Options;

            using (var context = new DatabaseContext(options))
            {
                context.Charger.RemoveRange(context.Charger);
                
                context.Charger.Add(new Charger 
                { 
                    Id = 1,
                    CreatedAt = DateTime.Now,
                    CreatedBy = new User { Email = "test", FirstName = "test", LastName = "test", Password = "test" },
                    Active = true,
                    Events = new List<Event> { },
                    LastSyncAt = DateTime.Now,
                    Latitude = 1.0,
                    Longitude = 1.0,
                    Name = "Test Charger 1"
                });

                context.Charger.Add(new Charger
                {
                    Id = 2,
                    CreatedAt = DateTime.Now,
                    CreatedBy = new User { Email = "test", FirstName = "test", LastName = "test", Password = "test" },
                    Active = true,
                    Events = new List<Event> { },
                    LastSyncAt = DateTime.Now,
                    Latitude = 1.0,
                    Longitude = 1.0,
                    Name = "Test Charger 2"
                });

                context.SaveChanges();
            }

            using (var context = new DatabaseContext(options))
            {
                ChargerController chargerController = new ChargerController(context, new HttpClient());
            
                // Act
                var result = (await chargerController.GetChargers()).Result as ObjectResult;

                // Assert
                Assert.AreEqual(2, (result.Value as List<Charger>).Count);
            }
        }

        [TestMethod]
        public async Task GetChargerByID_WhenThereIsNoChargerWithGivenID_ShouldReturnError404()
        {
            // Arrange
            var options = new DbContextOptionsBuilder<DatabaseContext>()
                .UseInMemoryDatabase(databaseName: "WattsUpDatabase")
                .Options;

            using (var context = new DatabaseContext(options))
            {
                context.Charger.RemoveRange(context.Charger);

                context.Charger.Add(new Charger
                {
                    Id = 1,
                    CreatedAt = DateTime.Now,
                    CreatedBy = new User { Email = "test", FirstName = "test", LastName = "test", Password = "test" },
                    Active = false,
                    Events = new List<Event> { },
                    LastSyncAt = DateTime.Now,
                    Latitude = 1.0,
                    Longitude = 1.0,
                    Name = "Test Charger 1"
                });

                context.SaveChanges();
            }

            using (var context = new DatabaseContext(options))
            {
                ChargerController chargerController = new ChargerController(context, new HttpClient());

                // Act
                var chargers = await chargerController.GetChargerByID(9999);
                var result = chargers.Result as StatusCodeResult;

                // Assert
                Assert.AreEqual(404, result.StatusCode);
            }
        }

        [TestMethod]
        public async Task GetChargerByID_WhenThereIsChargerWithGivenID_ShouldReturnThatCharger()
        {
            // Arrange
            var options = new DbContextOptionsBuilder<DatabaseContext>()
                .UseInMemoryDatabase(databaseName: "WattsUpDatabase")
                .Options;

            using (var context = new DatabaseContext(options))
            {
                context.Charger.RemoveRange(context.Charger);

                context.Charger.Add(new Charger
                {
                    Id = 99,
                    CreatedAt = DateTime.Now,
                    CreatedBy = new User { Email = "test", FirstName = "test", LastName = "test", Password = "test" },
                    Active = true,
                    Events = new List<Event> { },
                    LastSyncAt = DateTime.Now,
                    Latitude = 1.0,
                    Longitude = 1.0,
                    Name = "Test Charger 2"
                });

                context.SaveChanges();
            }

            using (var context = new DatabaseContext(options))
            {
                ChargerController chargerController = new ChargerController(context, new HttpClient());

                // Act
                var result = (await chargerController.GetChargerByID(99)).Result as ObjectResult;

                // Assert
                Assert.AreEqual(99, (result.Value as Charger).Id);
            }
        }

        [TestMethod]
        public async Task GetChargersByState_Active_WhenThereAreActiveChargers_ShouldReturnActiveChargers()
        {
            // Arrange
            var options = new DbContextOptionsBuilder<DatabaseContext>()
                .UseInMemoryDatabase(databaseName: "WattsUpDatabase")
                .Options;

            using (var context = new DatabaseContext(options))
            {
                context.Charger.RemoveRange(context.Charger);

                context.Charger.Add(new Charger
                {
                    Id = 1,
                    CreatedAt = DateTime.Now,
                    CreatedBy = new User { Email = "test", FirstName = "test", LastName = "test", Password = "test" },
                    Active = true,
                    Events = new List<Event> { },
                    LastSyncAt = DateTime.Now,
                    Latitude = 1.0,
                    Longitude = 1.0,
                    Name = "Test Charger 1"
                });
                context.Charger.Add(new Charger
                {
                    Id = 2,
                    CreatedAt = DateTime.Now,
                    CreatedBy = new User { Email = "test", FirstName = "test", LastName = "test", Password = "test" },
                    Active = false,
                    Events = new List<Event> { },
                    LastSyncAt = DateTime.Now,
                    Latitude = 1.0,
                    Longitude = 1.0,
                    Name = "Test Charger 2"
                });
                context.Charger.Add(new Charger
                {
                    Id = 3,
                    CreatedAt = DateTime.Now,
                    CreatedBy = new User { Email = "test", FirstName = "test", LastName = "test", Password = "test" },
                    Active = true,
                    Events = new List<Event> { },
                    LastSyncAt = DateTime.Now,
                    Latitude = 1.0,
                    Longitude = 1.0,
                    Name = "Test Charger 3"
                });

                context.SaveChanges();
            }

            using (var context = new DatabaseContext(options))
            {
                ChargerController chargerController = new ChargerController(context, new HttpClient());

                // Act
                var result = (await chargerController.GetChargersByState(true)).Result as ObjectResult;

                // Assert
                Assert.AreEqual("Test Charger 1", (result.Value as List<Charger>).FirstOrDefault().Name);
                Assert.AreEqual("Test Charger 3", (result.Value as List<Charger>).Last().Name);
            }
        }

        [TestMethod]
        public async Task GetChargersByState_NotActive_WhenThereAreNonActiveChargers_ShouldReturnNonActiveChargers()
        {
            // Arrange
            var options = new DbContextOptionsBuilder<DatabaseContext>()
                .UseInMemoryDatabase(databaseName: "WattsUpDatabase")
                .Options;

            using (var context = new DatabaseContext(options))
            {
                context.Charger.RemoveRange(context.Charger);

                context.Charger.Add(new Charger
                {
                    Id = 1,
                    CreatedAt = DateTime.Now,
                    CreatedBy = new User { Email = "test", FirstName = "test", LastName = "test", Password = "test" },
                    Active = false,
                    Events = new List<Event> { },
                    LastSyncAt = DateTime.Now,
                    Latitude = 1.0,
                    Longitude = 1.0,
                    Name = "Test Charger 1"
                });
                context.Charger.Add(new Charger
                {
                    Id = 2,
                    CreatedAt = DateTime.Now,
                    CreatedBy = new User { Email = "test", FirstName = "test", LastName = "test", Password = "test" },
                    Active = true,
                    Events = new List<Event> { },
                    LastSyncAt = DateTime.Now,
                    Latitude = 1.0,
                    Longitude = 1.0,
                    Name = "Test Charger 2"
                });
                context.Charger.Add(new Charger
                {
                    Id = 3,
                    CreatedAt = DateTime.Now,
                    CreatedBy = new User { Email = "test", FirstName = "test", LastName = "test", Password = "test" },
                    Active = false,
                    Events = new List<Event> { },
                    LastSyncAt = DateTime.Now,
                    Latitude = 1.0,
                    Longitude = 1.0,
                    Name = "Test Charger 3"
                });

                context.SaveChanges();
            }

            using (var context = new DatabaseContext(options))
            {
                ChargerController chargerController = new ChargerController(context, new HttpClient());

                // Act
                var result = (await chargerController.GetChargersByState(false)).Result as ObjectResult;

                // Assert
                Assert.AreEqual("Test Charger 1", (result.Value as List<Charger>).FirstOrDefault().Name);
                Assert.AreEqual("Test Charger 3", (result.Value as List<Charger>).Last().Name);
            }
        }

        [TestMethod]
        public async Task AddCharger_GivenValidData_ShouldReturnSuccess()
        {
            // Arrange
            var options = new DbContextOptionsBuilder<DatabaseContext>()
                .UseInMemoryDatabase(databaseName: "WattsUpDatabase")
                .Options;

            using (var context = new DatabaseContext(options))
            {
                context.Charger.RemoveRange(context.Charger);
                context.SaveChanges();
            }

            using (var context = new DatabaseContext(options))
            {
                ChargerController chargerController = new ChargerController(context, new HttpClient());

                // Act
                var response = await chargerController.AddCharger(new Charger
                {
                    Id = 1,
                    CreatedAt = DateTime.Now,
                    CreatedBy = new User { Email = "test", FirstName = "test", LastName = "test", Password = "test" },
                    Active = false,
                    Events = new List<Event> { },
                    LastSyncAt = DateTime.Now,
                    Latitude = 1.0,
                    Longitude = 1.0,
                    Name = "Test Charger 1"
                });

                // Assert
                Assert.AreEqual(1, context.Charger.Count());
            }
        }

        [TestMethod]
        public async Task UpdateCharger_GivenChargerExists_ShouldReturnUpdatedCharger()
        {
            // Arrange
            var options = new DbContextOptionsBuilder<DatabaseContext>()
                .UseInMemoryDatabase(databaseName: "WattsUpDatabase")
                .Options;

            using (var context = new DatabaseContext(options))
            {
                context.Charger.RemoveRange(context.Charger);

                context.Charger.Add(new Charger
                {
                    Id = 1,
                    CreatedAt = DateTime.Now,
                    CreatedBy = new User { Email = "test", FirstName = "test", LastName = "test", Password = "test" },
                    Active = false,
                    Events = new List<Event> { },
                    LastSyncAt = DateTime.Now,
                    Latitude = 1.0,
                    Longitude = 1.0,
                    Name = "Test Charger 1"
                });

                context.SaveChanges();
            }

            using (var context = new DatabaseContext(options))
            {
                ChargerController chargerController = new ChargerController(context, new HttpClient());

                // Act
                var response = await chargerController.UpdateChargerByID(1, new Charger
                {
                    Id = 1,
                    CreatedAt = DateTime.Now,
                    CreatedBy = new User { Email = "test", FirstName = "test", LastName = "test", Password = "test" },
                    Events = new List<Event> { },
                    Active = false,
                    LastSyncAt = DateTime.Now,
                    Latitude = 1.0,
                    Longitude = 1.0,
                    Name = "TESTNOW"
                });

                // Assert
                Assert.AreEqual("TESTNOW", context.Charger.FirstOrDefault().Name);
            }
        }
    }
}