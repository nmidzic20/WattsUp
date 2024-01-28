namespace backend.Models.Entities
{
    public class Charger
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public double Latitude { get; set; }
        public double Longitude { get; set; }
        public DateTime CreatedAt { get; set; }
        public int CreatedById { get; set; }
        public virtual User CreatedBy { get; set; }
        public DateTime LastSyncAt { get; set; }
        public bool Active { get; set; }

        public virtual ICollection<Event> Events { get; set; }
    }
}
