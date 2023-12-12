using backend.Data;
using backend.Models.Entities;
using backend.Models.Requests;
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

        [HttpPost]
        public async Task<ActionResult<Card>> PostCard(CardCreateRequest _card)
        {
            try {
                var res = await _dbContext.Card
                .Where(c => c.Value == _card.Value)
                .FirstOrDefaultAsync();

                if (res != null) {
                    return Conflict("Card with the same value already exists");
                }

                var card = new Card {
                    OwnedById = _card.OwnedById,
                    Value = _card.Value,
                    Active = true
                };
                _dbContext.Card.Add(card);
                await _dbContext.SaveChangesAsync();

                return Ok(card);

            }catch (Exception ex) {
                return StatusCode(500, $"An error occured: {ex.Message}");
            }
            
        }

        [HttpPut("cardId")]
        public async Task<ActionResult<Card>> PutCard(long cardID, Card _card)
        {
            try {
                var card = await _dbContext.Card
                .Where(c => c.Id == cardID)
                .FirstOrDefaultAsync();

                if(card == null) {
                    return NotFound("Specified card doesn't exist.");
                }

                card.OwnedById = _card.OwnedById;
                card.Value = _card.Value;
                card.Active = _card.Active;

                await _dbContext.SaveChangesAsync();

                return Ok(card);

            } catch (Exception ex) {
                return StatusCode(500, $"An error occured: {ex.Message}");
            }
        }

        [HttpDelete("cardId")]
        public async Task<ActionResult<Card>> DeleteCard(long cardID) {
            try {
                var card = await _dbContext.Card
                .Where(c => c.Id == cardID)
                .FirstOrDefaultAsync();

                if (card == null) {
                    return NotFound("Specified card doesn't exist.");
                }

                _dbContext.Card.Remove(card);
                await _dbContext.SaveChangesAsync();

                return Ok(card);

            } catch (Exception ex) {
                return StatusCode(500, $"An error occured: {ex.Message}");
            }
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
