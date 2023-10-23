using Microsoft.EntityFrameworkCore;

namespace backend.Models.Entities
{
    [PrimaryKey(nameof(ChargerId), nameof(CardId))]
    public class Event
    {
        public int ChargerId { get; set; }
        public int CardId { get; set; }
        public DateTime StartedAt { get; set; }
        public DateTime EndedAt { get; set; }
        public double VolumeKwh { get; set; }

        public virtual Charger Charger { get; set; }
        public virtual Card Card { get; set; }
    }
}
