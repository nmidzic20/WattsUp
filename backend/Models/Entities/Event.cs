using Microsoft.EntityFrameworkCore;

namespace backend.Models.Entities
{
    public class Event
    {
        public int Id { get; set; }
        public int ChargerId { get; set; }
        public int CardId { get; set; }
        public DateTime StartedAt { get; set; }
        public DateTime EndedAt { get; set; }
        public double VolumeKwh { get; set; }

        public virtual Charger Charger { get; set; }
        public virtual Card Card { get; set; }
    }
}
