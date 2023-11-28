namespace backend.Models.Entities
{
    public class Card
    {
        public int Id { get; set; }
        public int OwnedById { get; set; }
        public virtual User OwnedBy { get; set; }
        public string Value { get; set; }
        public bool Active { get; set; }

        public virtual ICollection<Event> Events { get; set; }
    }
}
