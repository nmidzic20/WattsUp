using backend.Data;
using backend.Models.Entities;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class CardController : ControllerBase
    {
        private readonly DatabaseContext _dbContext;

        public CardController(DatabaseContext dbContext)
        {
            _dbContext = dbContext;
        }

        [HttpGet]
        public async Task<ActionResult<List<Card>>> GetCards()
        {
            var res = await _dbContext.Card
                .ToListAsync();

            return Ok(res);
        }

        [HttpGet("{userId}")]
        public async Task<ActionResult<List<Card>>> GetCardsForUser(long userId)
        {
            if (!await _dbContext.User.AnyAsync(u => u.Id == userId))
            {
                return NotFound("Specified user doesn't exist.");
            }

            var res = await _dbContext.Card
                .Where(c => c.OwnedBy.Id == userId)
                .ToListAsync();

            return Ok(res);
        }
    }
}
