using backend.Data;
using System.Net;
using System.Net.Mail;
using backend.Models.Entities;
using System.Security.Cryptography;
using NuGet.Common;
using Microsoft.AspNetCore.Identity;

namespace backend.Services {
    public class EmailService {
        private readonly SmtpClient _mailingClient;

        public EmailService() {
            _mailingClient = new SmtpClient("smtp.gmail.com");
            _mailingClient.Port = 587;
            _mailingClient.EnableSsl = true;
            _mailingClient.Credentials = new NetworkCredential("filip8880@gmail.com", "ygqb ydub kdvq cwzf");
        }
        public async Task<string> SendForgotMyPasswordEmail(User user)
        {
            string userToken = GenerateTokenForUser();
            string passwordResetMessage = "Please click on the following link to reset your password: \n" + 
                "Tu bu trebalo deti link na frontu za reset/" + userToken;
            MailMessage mailMessage = new MailMessage("Wattsup@mailingservice.com", user.Email, "Password reset", passwordResetMessage);
            try {
                await _mailingClient.SendMailAsync(mailMessage);
                return userToken;
            } catch{
                throw;   
            }
        }

        private string GenerateTokenForUser() {
            const string allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            byte[] randomBytes = new byte[16 * 4]; // 4 bytes per char for better randomness

            using (var rng = RandomNumberGenerator.Create()) {
                rng.GetBytes(randomBytes);
            }

            char[] chars = new char[16];
            for (int i = 0; i < 16; i++) {
                uint randomInt = BitConverter.ToUInt32(randomBytes, i * 4);
                chars[i] = allowedChars[(int)(randomInt % (uint)allowedChars.Length)];
            }

            return new string(chars);
        }
    }
}
