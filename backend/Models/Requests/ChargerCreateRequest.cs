using System.ComponentModel.DataAnnotations;

namespace backend.Models.Requests
{
    public class ChargerCreateRequest
    {
        [Required]
        public string Name { get; set; }
        [Required]
        public double Latitude { get; set; }
        [Required]
        public double Longitude { get; set; }
        [Required]
        public int CreatedById { get; set; }
    }
}
