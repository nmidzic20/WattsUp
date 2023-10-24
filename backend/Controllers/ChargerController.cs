using backend.Models.Entities;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ChargerController : Controller
    {
        private readonly WattsUpDbContext _dbContext;
        private HttpClient _client;

        public ChargerController(WattsUpDbContext dbContext, HttpClient httpClient)
        {
            _dbContext = dbContext;
            _client = httpClient;
        }

        [HttpPost]
        public async Task<ActionResult<Charger>> AddCharger([FromBody] Charger _charger)
        {
            var charger = new Charger
            {
                Name = _charger.Name,
                Latitude = _charger.Latitude,
                Longitude = _charger.Longitude,
                CreatedAt = DateTime.Now,
                CreatedBy = _charger.CreatedBy,
                LastSyncAt = DateTime.Now,
                Active = false
            };

            _dbContext.Sensors.Add(charger);

            await _dbContext.SaveChangesAsync();

            return Ok(new Charger
            {
                Id = charger.Id,
                Name = charger.Name,
                Latitude = charger.Latitude,
                Longitude = charger.Longitude,
                CreatedAt = charger.CreatedAt,
                CreatedBy = charger.CreatedBy,
                LastSyncAt = charger.LastSyncAt,
                Active = charger.Active
            });
        }
    }
}
