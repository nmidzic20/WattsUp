using System.ComponentModel.DataAnnotations;

namespace backend.Models.Requests
{
    public class CardCreateRequest
    {
        [Required]
        public string Value { get; set; }
        public int OwnedById { get; set; }
    }
}
