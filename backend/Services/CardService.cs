using backend.Data;
using backend.Models.Entities;
using backend.Models.Requests;
using Microsoft.EntityFrameworkCore;

namespace backend.Services
{
    public class CardService
    {
        private readonly DatabaseContext _context;

        public CardService(DatabaseContext context)
        {
            _context = context;
        }

        public async Task<Card> CreateCardAsync(CardCreateRequest card, User user)
        {
            var existingCard = await _context.Card.FirstOrDefaultAsync(c => c.Value == card.Value);
            if (existingCard != null)
            {
                return null;
            }

            Card newCard = new Card
            {
                OwnedBy = user,
                Value = card.Value,
                Active = true
            };

            return newCard;
        }

        public async Task<bool> CardBelongsToUser(Card card, User user)
        {
            var existingCard = await _context.Card.FirstOrDefaultAsync(c => c.Value == card.Value);
            var existingUser = await _context.User.FirstOrDefaultAsync(u => u.Email == user.Email);
            
            if (existingCard == null || existingUser == null || existingCard.OwnedBy.Id != existingUser.Id)
            {
                return false;
            }

            return true;
        }
    }
}
