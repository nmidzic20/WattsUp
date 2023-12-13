using backend.Models.Entities;

namespace backend.Models.Responses
{
    public class ChargersResponse
    {
        public string Name { get; set; }
        public double Latitude { get; set; }
        public double Longitude { get; set; }
        public DateTime CreatedAt { get; set; }
        public string CreatedBy { get; set; }
        public DateTime LastSyncAt { get; set; }
        public bool Active { get; set; }
    }
}
