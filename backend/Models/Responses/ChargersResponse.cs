using backend.Models.Entities;

namespace backend.Models.Responses
{
    public class ChargersResponse
    {
        public int id { get; set; }
        public string name { get; set; }
        public double latitude { get; set; }
        public double longitude { get; set; }
        public DateTime createdAt { get; set; }
        public string createdBy { get; set; }
        public DateTime lastSyncAt { get; set; }
        public bool active { get; set; }
    }
}
