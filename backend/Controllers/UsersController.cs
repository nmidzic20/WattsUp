using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using backend.Data;
using backend.Models.Entities;
using backend.Models.Requests;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.Identity;
using backend.Services;
using Microsoft.AspNetCore.Authorization;
using backend.Models.Responses;

namespace backend.Controllers {
    [Route("api/[controller]")]
    [ApiController]
    public class UsersController : ControllerBase {
        private readonly DatabaseContext _context;
        private readonly UserService _userService;
        private readonly CardService _cardService;
        private readonly EmailService _emailService;

        public UsersController(DatabaseContext context, UserService userService, CardService cardService, EmailService emailService) {
            _context = context;
            _userService = userService;
            _cardService = cardService;
            _emailService = emailService;
        }

        // POST: api/Users/Login
        [HttpPost("Login")]
        public async Task<ActionResult<User>> Login(UserLoginRequest userRequest) {
            if (!ModelState.IsValid) {
                return BadRequest(ModelState);
            }

            try {
                LoginResponse response = await _userService.LoginAsync(userRequest);

                return Ok(response);
            } catch (InvalidDataException ex) {
                return Unauthorized(new { message = ex.Message });
            } catch (Exception ex) {
                return NotFound(new { message = ex.Message });
            }
        }

        // POST: api/Users/TokenRefresh
        [Authorize(AuthenticationSchemes = "NoExpiryCheck")]
        [HttpPost("TokenRefresh")]
        public async Task<ActionResult<User>> TokenRefresh(TokenRefreshRequest tokenRefreshRequest) {
            if (!ModelState.IsValid) {
                return BadRequest(ModelState);
            }

            try {
                LoginResponse response = await _userService.TokenRefreshAsync(tokenRefreshRequest);

                return Ok(response);
            } catch (Exception ex) {
                return Unauthorized(new { message = ex.Message });
            }

        }

        // POST: api/Users
        [HttpPost]
        public async Task<ActionResult<User>> PostUser(UserCreateRequest userRequest) {
            if (!ModelState.IsValid) {
                return BadRequest(ModelState);
            }

            User newUser = await _userService.CreateUserAsync(userRequest);
            if (newUser == null) {
                return Conflict(new { message = "A user with this email already exists." });
            }

            Card newCard = null;
            if (userRequest.Card != null) {
                newCard = await _cardService.CreateCardAsync(userRequest.Card, newUser);
                if (newCard == null) {
                    return Conflict(new { message = "This card already exists." });
                }
            }

            _context.User.Add(newUser);
            if (newCard != null) {
                _context.Card.Add(newCard);
            }
            await _context.SaveChangesAsync();

            return Ok(newUser);
        }

        //POST: api/Users/ResetPassword
        [HttpPost("ResetPassword")]
        public async Task<ActionResult<User>> ResetUserPasswordRequest(UserResetPasswordRequest userRequest) {
            User user = await _userService.GetUserAsync(userRequest.Email);
            if (user == null) {
                return NotFound(new { message = "No users with specified Email exist." });
            } else {
                try {
                    await _emailService.SendForgotMyPasswordEmail(user);
                    return Ok();
                } catch{
                    return StatusCode(500, "Failed to send password reset email");
                }
            }
        }
    }
}
