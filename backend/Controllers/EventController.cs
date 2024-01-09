using backend.Data;
using backend.Models.Entities;
using backend.Models.Requests;
using backend.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Net;

namespace backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class EventController : ControllerBase
    {
        private readonly DatabaseContext _dbContext;
        private readonly HttpClient _client;
        private readonly SSEService _sseService;

        public EventController(DatabaseContext dbContext, HttpClient httpClient, SSEService sseService)
        {
            _dbContext = dbContext;
            _client = httpClient;
            _sseService=sseService;
        }

        [Authorize(Policy = "Admin")]
        [HttpGet]
        public async Task<ActionResult<List<Event>>> GetEvents()
        {
            var res = await _dbContext.Event
                .ToListAsync();

            return Ok(res);
        }

        [Authorize]
        [HttpGet("forCard/{cardId}")]
        public async Task<ActionResult<List<Event>>> GetEventsForCard(long cardId)
        {
            if (!await CheckCard(cardId))
            {
                return NotFound("Specified card doesn't exist.");
            }

            var res = await _dbContext.Event
                .Where(e => e.CardId == cardId)
                .ToListAsync();

            return Ok(res);
        }

        [Authorize(Policy = "Admin")]
        [HttpGet("forCharger/{chargerId}")]
        public async Task<ActionResult<List<Event>>> GetEventsForCharger(long chargerId)
        {
            if (!await CheckCharger(chargerId))
            {
                return NotFound("Specified charger doesn't exist.");
            }

            var res = await _dbContext.Event
                .Where(e => e.ChargerId == chargerId)
                .ToListAsync();

            return Ok(res);
        }

        [HttpGet("current")]
        public async Task<ActionResult<List<Event>>> GetCurrentEvents()
        {
            // ongoing events have an EndedAt max value placeholder
            var res = await _dbContext.Event
                .Where(e => e.EndedAt == DateTime.MaxValue)
                .ToListAsync();

            return Ok(res);
        }

        [HttpPost]
        public async Task<ActionResult<Event>> CreateEvent(EventCreateRequest eventRequest)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (!await CheckCard(eventRequest.CardId))
            {
                return NotFound("Specified card doesn't exist.");
            }

            if (!await CheckCharger(eventRequest.ChargerId))
            {
                return NotFound("Specified charger doesn't exist.");
            }

            var newEvent = new Event
            {
                ChargerId = eventRequest.ChargerId,
                CardId = eventRequest.CardId,
                VolumeKwh = 0,
                StartedAt = DateTime.UtcNow,
                EndedAt = DateTime.MaxValue
            };

            if (!await UpdateChargerState(true, eventRequest.ChargerId))
            {
                return Conflict("Failed to update charger state.");
            }

            _dbContext.Event.Add(newEvent);
            await _dbContext.SaveChangesAsync();
            return Ok(newEvent);
        }

        [HttpPut]
        public async Task<ActionResult<Event>> EndEvent(EventEndRequest eventEndRequest)
        {
            var eventToUpdate = await _dbContext.Event
                .Where(e => e.Id == eventEndRequest.Id)
                .FirstOrDefaultAsync();

            if (eventToUpdate == null)
            {
                return NotFound();
            }

            eventToUpdate.VolumeKwh = eventEndRequest.VolumeKwh;
            eventToUpdate.EndedAt = DateTime.UtcNow;

            if (!await UpdateChargerState(false, eventToUpdate.ChargerId))
            {
                return Conflict("Failed to update charger state.");
            }

            await _dbContext.SaveChangesAsync();
            return Ok(eventToUpdate);
        }

        private async Task<bool> UpdateChargerState(bool state, long id)
        {
            var chargerController = new ChargerController(_dbContext, _client, _sseService);
            var result = (await chargerController.GetChargerByID(id)).Result as ObjectResult;

            if (result.Value is not Charger charger)
            {
                return false;
            }

            charger.Active = state;
            charger.LastSyncAt = DateTime.UtcNow;
            var response = await chargerController.UpdateChargerByID(id, charger);

            return response.Result is OkObjectResult;
        }

        private Task<bool> CheckCard(long cardId)
        {
            return _dbContext.Card.AnyAsync(c => c.Id == cardId);
        }

        private Task<bool> CheckCharger(long chargerId)
        {
            return _dbContext.Charger.AnyAsync(c => c.Id == chargerId);
        }
    }
}
