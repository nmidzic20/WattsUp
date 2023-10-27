using backend.Data;
using backend.Models.Entities;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Collections.Immutable;

namespace backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ChargerController : Controller
    {
        private readonly DatabaseContext _dbContext;
        private HttpClient _client;

        public ChargerController(DatabaseContext dbContext, HttpClient httpClient)
        {
            _dbContext = dbContext;
            _client = httpClient;
        }

        [HttpGet]
        public async Task<ActionResult<List<Charger>>> GetChargers()
        {
            var chargers = await _dbContext.Charger
                .Include(c => c.Events)
                .Include(c => c.CreatedBy)
                .ToListAsync();

            return Ok(chargers);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<List<Charger>>> GetChargerByID(int id)
        {
            var chargers = await _dbContext.Charger
                .Include(c => c.Events)
                .Include(c => c.CreatedBy)
                .Where(c => c.Id == id)
                .ToListAsync();

            return Ok(chargers);
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
            
            _dbContext.Charger.Add(charger);

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
