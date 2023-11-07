using backend.Data;
using backend.Models.Entities;
using backend.Models.Requests;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Runtime.Loader;

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
            var res = await _dbContext.Event
                .Where(e => e.CardId == cardId)
                .ToListAsync();

            return Ok(res);
        }

        [HttpGet("forCharger/{chargerId}")]
        public async Task<ActionResult<List<Event>>> GetEventsForCharger(long chargerId)
        {
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

            var charger = await _dbContext.Charger
                .Where(c => c.Id == eventRequest.ChargerId)
                .FirstOrDefaultAsync();

            if (charger == null)
            {
                return Conflict("The specified charger doesn't exist.");
            }

            var card = await _dbContext.Card
                .Where(c => c.Id == eventRequest.CardId)
                .FirstOrDefaultAsync();

            if (card == null)
            {
                return Conflict("The specified card doesn't exist.");
            }

            var newEvent = new Event
            {
                ChargerId = eventRequest.ChargerId,
                CardId = eventRequest.CardId,
                VolumeKwh = eventRequest.VolumeKwh,
                StartedAt = DateTime.Now,
                EndedAt = DateTime.MaxValue
            };

            // call charging station update
            charger = new Charger
            {
                Active = true,
                LastSyncAt = DateTime.Now
            };

            var chargerUpdateResponse = await _client.PutAsJsonAsync("api/Charger/" + newEvent.ChargerId.ToString(), charger);
            
            if (chargerUpdateResponse != null)
            {
                return Conflict("Failed to update charger.");
            }

            _dbContext.Event.Add(newEvent);
            await _dbContext.SaveChangesAsync();

            return Ok(newEvent);
        }
    }
}
