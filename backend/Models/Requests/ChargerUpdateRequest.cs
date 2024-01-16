using System.ComponentModel.DataAnnotations;

namespace backend.Models.Requests
{
    public class ChargerUpdateRequest
    {
        [Required]
        public string Name { get; set; }
        [Required]
        public double Latitude { get; set; }
        [Required]
        public double Longitude { get; set; }
        [Required]
        public bool Active { get; set; }
    }
}
