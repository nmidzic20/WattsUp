namespace backend.Models.Entities
{
    public class User
    {
        public int Id { get; set; }
        public string FirstName { get; set; }
        public string LastName { get; set; }
        public string Email { get; set; }
        public string Password { get; set; }
        public bool Active { get; set; }
        public DateTime CreatedAt { get; set; }
        public int RoleId { get; set; }
        public virtual Role Role { get; set; }
        public int? RefreshTokenId { get; set; }
        public virtual RefreshToken? RefreshToken { get; set; }
        public string? PasswordResetToken {get; set; }
    }
}
