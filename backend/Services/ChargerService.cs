using backend.Data;
using backend.Models.Responses;
using Microsoft.EntityFrameworkCore;

namespace backend.Services
{
    public class ChargerService
    {
        private readonly DatabaseContext _context;

        public ChargerService(DatabaseContext context)
        {
            _context=context;
        }

        public async Task<List<ChargersResponse>> GetChargers()
        {
            var chargers = await _context.Charger.Include(c => c.CreatedBy).ToListAsync();

            var response = chargers.Select(charger => new ChargersResponse
            {
                id = charger.Id,
                name = charger.Name,
                latitude = charger.Latitude,
                longitude = charger.Longitude,
                createdAt = charger.CreatedAt,
                createdBy = charger.CreatedBy.Email,
                lastSyncAt = charger.LastSyncAt,
                active = charger.Active
            }).ToList();

            return response;
        }
    }
}
