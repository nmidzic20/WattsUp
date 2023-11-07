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
        [TestMethod]
        public async Task GetEvents_WhenThereAreNoEvents_ShouldReturnEmptyList()
        {
        }
    }
}