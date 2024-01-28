using System.ComponentModel.DataAnnotations;

namespace backend.Models.Requests
{
    public class TokenRefreshRequest
    {
        [Required]
        public string JWT { get; set; }
        [Required]
        public string RefreshToken { get; set; }
    }
}