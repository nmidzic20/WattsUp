using backend.Data;
using System.Net;
using System.Net.Mail;
using backend.Models.Entities;

namespace backend.Services {
    public class EmailService {
        private readonly SmtpClient _mailingClient;
        private readonly DatabaseContext _context;
        private readonly IConfiguration _configuration;

        public EmailService(DatabaseContext context, IConfiguration configuration) {
            _mailingClient = new SmtpClient("smtp.gmail.com");
            _mailingClient.Port = 587;
            _mailingClient.EnableSsl = true;
            _mailingClient.Credentials = new NetworkCredential("filip8880@gmail.com", "ygqb ydub kdvq cwzf");
            _context = context;
            _configuration = configuration;
        }
        public async Task SendForgotMyPasswordEmail(User user)
        {
            string passwordResetMessage = "Please click on the following link to reset your password: \n" + 
                "Tu bu trebalo deti link na frontu za reset";
            MailMessage mailMessage = new MailMessage("Wattsup@mailingservice.com", user.Email, "Password reset", passwordResetMessage);
            try {
                await _mailingClient.SendMailAsync(mailMessage);
            } catch (Exception ex){
                Console.WriteLine("Failed to send email: " + ex.Message);   
            }
        }
    }
}
