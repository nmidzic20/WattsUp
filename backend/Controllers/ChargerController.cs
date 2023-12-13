using backend.Data;
using backend.Models.Entities;
using backend.Models.Requests;
using backend.Models.Responses;
using Microsoft.AspNetCore.Authorization;
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

        [Authorize]
        [HttpGet]
        public async Task<ActionResult<List<Charger>>> GetChargers()
        {
            var chargers = await _dbContext.Charger.Include(c => c.CreatedBy).ToListAsync();

            var response = chargers.Select(charger => new ChargersResponse
            {
                Name = charger.Name,
                Latitude = charger.Latitude,
                Longitude = charger.Longitude,
                CreatedAt = charger.CreatedAt,
                CreatedBy = charger.CreatedBy.Email,
                LastSyncAt = charger.LastSyncAt,
                Active = charger.Active
            }).ToList();

            return Ok(response);
        }

        [Authorize]
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

        [Authorize]
        [HttpGet("byState/{active}")]
        public async Task<ActionResult<List<Charger>>> GetChargersByState(bool active)
        {
            var chargers = await _dbContext.Charger
                .Where(c => c.Active == active)
                .ToListAsync();

            return Ok(chargers);
        }

        [Authorize(Policy = "Admin")]
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
                CreatedAt = DateTime.UtcNow,
                CreatedById = _charger.CreatedById,
                CreatedBy = user,
                LastSyncAt = DateTime.UtcNow,
                Active = false
            };
            // TODO: add valid check
            _dbContext.Charger.Add(charger);
            await _dbContext.SaveChangesAsync();

            return Ok(charger);
        }

        [Authorize(Policy = "Admin")]
        [HttpPut("{id}")]
        public async Task<ActionResult<Charger>> UpdateChargerByID(long id, Charger _charger)
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
            charger.LastSyncAt = DateTime.UtcNow;
            charger.Active = _charger.Active;

            await _dbContext.SaveChangesAsync();
            
            return Ok(charger);
        }

        [Authorize(Policy = "Admin")]
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

            return Ok(charger);
        }
    }
}
