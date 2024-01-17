namespace backend.Models.Requests {
    public class UserResetPasswordRequest {
        public string token { get; set; }
        public string password { get; set; }
    }
}
