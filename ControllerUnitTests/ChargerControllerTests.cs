using backend.Controllers;
using backend.Data;
using backend.Models.Entities;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace ControllerUnitTests
{
    [TestClass]
    public class ChargerControllerTests
    {
        [TestMethod]
        public async Task Get_WhenThereAreChargers_ShouldReturnAllChargers()
        {
            // Arrange
            var options = new DbContextOptionsBuilder<DatabaseContext>()
                .UseInMemoryDatabase(databaseName: "MovieListDatabase")
                .Options;

            using (var context = new DatabaseContext(options))
            {
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
                var chargers = (await chargerController.GetChargers()).Result as OkObjectResult;

                // Assert
                Assert.AreEqual(2, (chargers.Value as List<Charger>).Count);
            }
        }
    }
}