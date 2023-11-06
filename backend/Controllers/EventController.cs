using backend.Data;
using backend.Models.Entities;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

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

        [HttpGet("{cardId}")]
        public async Task<ActionResult<List<Event>>> GetEventsForCard(long cardId)
        {
            var res = await _dbContext.Event
                .Where(e => e.CardId == cardId)
                .ToListAsync();

            return Ok(res);
        }
    }
}
