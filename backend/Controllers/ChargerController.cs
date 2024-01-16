using backend.Data;
using backend.Models.Entities;
using backend.Models.Requests;
using backend.Models.Responses;
using backend.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Runtime.InteropServices;
using System.Text.Json;

namespace backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ChargerController : ControllerBase
    {
        private readonly DatabaseContext _dbContext;
        private readonly SSEService _sse;

        public ChargerController(DatabaseContext dbContext, HttpClient httpClient, SSEService sse)
        {
            _dbContext = dbContext;
            _sse = sse;
        }

        [HttpGet]
        public async Task<ActionResult<List<Charger>>> GetChargers()
        {
            var chargers = await _dbContext.Charger.ToListAsync();

            return Ok(chargers);
        }

        [Authorize]
        [HttpGet("web")]
        public async Task<ActionResult<List<ChargersResponse>>> GetChargersWeb()
        {
            ChargerService chargerService = new ChargerService(_dbContext);
            var chargers = await chargerService.GetChargers();

            return Ok(chargers);
        }

        [HttpGet("SSE")]
        public async Task GetChargersSSE(CancellationToken cancellationToken)
        {
            Response.Headers.Add("Content-Type", "text/event-stream");

            while (!cancellationToken.IsCancellationRequested)
            {
                var chargers = await _sse.WaitForChargerChange();
                var eventData = JsonSerializer.Serialize(new { chargers });

                await Response.WriteAsync($"data: {eventData}\n\n");
                await Response.Body.FlushAsync();

                _sse.Reset();
            }
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

            ChargerService chargerService = new ChargerService(_dbContext);
            var chargers = await chargerService.GetChargers();
            _sse.NotifyChargerChange(chargers);

            return Ok(charger);
        }

        [Authorize(Policy = "Admin")]
        [HttpPut("{id}")]
        public async Task<ActionResult<Charger>> UpdateChargerByID(long id, ChargerUpdateRequest _charger)
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

            ChargerService chargerService = new ChargerService(_dbContext);
            var chargers = await chargerService.GetChargers();
            _sse.NotifyChargerChange(chargers);

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

            ChargerService chargerService = new ChargerService(_dbContext);
            var chargers = await chargerService.GetChargers();
            _sse.NotifyChargerChange(chargers);

            return Ok(charger);
        }
    }
}
