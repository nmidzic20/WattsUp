using System.ComponentModel.DataAnnotations;

namespace backend.Models.Requests
{
    public class EventCreateRequest
    {
        [Required]
        public int ChargerId { get; set; }
        [Required]
        public int CardId { get; set; }
        
    }
}
