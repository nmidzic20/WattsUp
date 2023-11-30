using System.ComponentModel.DataAnnotations;

namespace backend.Models.Requests
{
    public class EventEndRequest
    {
        [Required]
        public int Id { get; set; }
        [Required]
        public double VolumeKwh { get; set; }

    }
}
