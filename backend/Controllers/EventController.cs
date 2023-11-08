using backend.Data;
using backend.Models.Entities;
using backend.Models.Requests;
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

        public EventController(DatabaseContext dbContext, HttpClient httpClient)
        {
            _dbContext = dbContext;
            _client = httpClient;
        }

        [HttpGet]
        public async Task<ActionResult<List<Event>>> GetEvents()
        {
            var res = await _dbContext.Event
                .ToListAsync();

            return Ok(res);
        }

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
                VolumeKwh = eventRequest.VolumeKwh,
                StartedAt = DateTime.Now,
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

        [HttpPut("{id}")]
        public async Task<ActionResult<Event>> EndEvent(long id)
        {
            var eventToUpdate = await _dbContext.Event
                .Where(e => e.Id == id)
                .FirstOrDefaultAsync();

            if (eventToUpdate == null)
            {
                return NotFound();
            }

            eventToUpdate.EndedAt = DateTime.Now;

            if (!await UpdateChargerState(false, eventToUpdate.ChargerId))
            {
                return Conflict("Failed to update charger state.");
            }

            await _dbContext.SaveChangesAsync();
            return Ok(eventToUpdate);
        }

        private async Task<bool> UpdateChargerState(bool state, long id)
        {
            // call charging station update
            var charger = new Charger
            {
                Active = state,
                LastSyncAt = DateTime.Now
            };

            var chargerController = new ChargerController(_dbContext, _client);
            var chargerUpdate = await chargerController.UpdateChargerByID(id, charger);

            // check response
            return chargerUpdate.Value != null;
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
