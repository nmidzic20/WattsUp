using backend.Data;
using backend.Models.Entities;
using backend.Models.Requests;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ChargerController : ControllerBase
    {
        private readonly DatabaseContext _dbContext;

        public ChargerController(DatabaseContext dbContext, HttpClient httpClient)
        {
            _dbContext = dbContext;
        }

        [HttpGet]
        public async Task<ActionResult<List<Charger>>> GetChargers()
        {
            var chargers = await _dbContext.Charger
                .ToListAsync();

            return Ok(chargers);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<Charger>> GetChargerByID(long id)
        {
            var charger = await _dbContext.Charger
                .Where(c => c.Id == id)
                .FirstOrDefaultAsync();
            
            if (charger == null)
            {
                return NotFound();
            }

            return Ok(charger);
        }

        [HttpGet("byState/{active}")]
        public async Task<ActionResult<List<Charger>>> GetChargersByState(bool active)
        {
            var chargers = await _dbContext.Charger
                .Where(c => c.Active == active)
                .ToListAsync();

            return Ok(chargers);
        }

        [HttpPost]
        public async Task<ActionResult<Charger>> AddCharger(ChargerCreateRequest _charger)
        {
            var user = await _dbContext.User
                .Where(u => u.Id == _charger.CreatedById)
                .FirstOrDefaultAsync();

            if (user == null)
            {
                return NotFound("Specified user doesn't exist.");
            }

            var charger = new Charger
            {
                Name = _charger.Name,
                Latitude = _charger.Latitude,
                Longitude = _charger.Longitude,
                CreatedAt = DateTime.Now,
                CreatedById = _charger.CreatedById,
                CreatedBy = user,
                LastSyncAt = DateTime.Now,
                Active = false
            };
            
            _dbContext.Charger.Add(charger);
            await _dbContext.SaveChangesAsync();

            return Ok(charger);
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<Charger>> UpdateChargerByID(long id, ChargerCreateRequest _charger)
        {
            var charger = await _dbContext.Charger
                .Where(c => c.Id == id)
                .FirstOrDefaultAsync();

            if (charger == null)
            {
                return NotFound();
            }

            charger.Name = _charger.Name;
            charger.Latitude = _charger.Latitude;
            charger.Longitude = _charger.Longitude;
            charger.LastSyncAt = DateTime.Now;
            charger.Active = _charger.Active;

            await _dbContext.SaveChangesAsync();
            
            return Ok($"Updated charger {charger.Id}");
        }

        [HttpDelete("{id}")]
        public async Task<ActionResult<Charger>> DeleteChargerByID(long id)
        {
            var charger = await _dbContext.Charger
                .Where(c => c.Id == id)
                .FirstOrDefaultAsync();

            if (charger == null)
            {
                return NotFound();
            }

            _dbContext.Charger.Remove(charger);
            await _dbContext.SaveChangesAsync();

            return Ok($"Deleted charger {charger.Id}");
        }
    }
}
