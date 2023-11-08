namespace backend.Models.Responses
{
    public class LoginResponse
    {
        public string JWT { get; set; }
        public string RefreshToken { get; set; }
        public DateTime RefreshTokenExpiresAt { get; set; }
    }
}
