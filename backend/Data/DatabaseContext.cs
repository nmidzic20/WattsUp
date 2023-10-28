using backend.Models.Entities;
using Microsoft.EntityFrameworkCore;

namespace backend.Data
{
    public class DatabaseContext : DbContext
    {
        public DatabaseContext(DbContextOptions<DatabaseContext> options): base(options)
        {
        }

        public DbSet<User> User { get; set; }
        public DbSet<Event> Event { get; set; }
        public DbSet<Charger> Charger { get; set; }
        public DbSet<Card> Card { get; set; }
        public DbSet<Role> Role { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Role>().HasData(
                new Role() { Id = 1, Name = "Admin" },
                new Role() { Id = 2, Name = "User" }
            );

            modelBuilder.Entity<User>().HasData(
                new User()
                {
                    Id = 1,
                    FirstName = "Admin",
                    LastName = "Admin",
                    Email = "admin@gmail.com",
                    Password = "123456",
                    Active = true,
                    CreatedAt = DateTime.Now,
                    RoleId = 1,
                },
                new User()
                {
                    Id = 2,
                    FirstName = "User",
                    LastName = "User",
                    Email = "user@gmail.com",
                    Password = "123456",
                    Active = true,
                    CreatedAt = DateTime.Now.AddHours(1),
                    RoleId = 2,
                },
                new User()
                {
                    Id = 3,
                    FirstName = "John",
                    LastName = "Doe",
                    Email = "john.doe@gmail.com",
                    Password = "123456",
                    Active = true,
                    CreatedAt = DateTime.Now.AddHours(2),
                    RoleId = 2,
                }
            );

            modelBuilder.Entity<Card>().HasData(
                new Card()
                {
                    Id = 1,
                    OwnedById = 1,
                    Value = "0x1A2B3C4F5D6E",
                    Active = true,
                },
                new Card()
                {
                    Id = 2,
                    OwnedById = 2,
                    Value = "0x4F5D6E1A2B3C",
                    Active = true,
                },
                new Card()
                {
                    Id = 3,
                    OwnedById = 2,
                    Value = "0x7F8A9D4F5D6E",
                    Active = true,
                },
                new Card()
                {
                    Id = 4,
                    OwnedById = 3,
                    Value = "0x7F8A9D1A2B3C",
                    Active = true,
                }
            );

            modelBuilder.Entity<Charger>().HasData(
                new Charger()
                {
                    Id = 1,
                    Name = "Charger Dorm",
                    Latitude = 46.309735,
                    Longitude = 16.348593,
                    CreatedAt = DateTime.Now,
                    CreatedById = 1,
                    LastSyncAt = DateTime.Now.AddHours(1),
                    Active = true,
                },
                new Charger()
                {
                    Id = 2,
                    Name = "Charger Mobilisis",
                    Latitude = 46.287309,
                    Longitude = 16.321733,
                    CreatedAt = DateTime.Now,
                    CreatedById = 1,
                    LastSyncAt = DateTime.Now.AddHours(2),
                    Active = true,
                },
                new Charger()
                {
                    Id = 3,
                    Name = "Charger FOI",
                    Latitude = 46.307790,
                    Longitude = 16.338061,
                    CreatedAt = DateTime.Now,
                    CreatedById = 1,
                    LastSyncAt = DateTime.Now.AddHours(3),
                    Active = true,
                }
            );

            modelBuilder.Entity<Event>().HasData(
                new Event()
                {
                    Id = 1,
                    ChargerId = 1, // Charger Dorm
                    CardId = 1, // Admin's card
                    StartedAt = DateTime.Now.AddHours(-2),
                    EndedAt = DateTime.Now.AddMinutes(30),
                    VolumeKwh = 9.8,
                },
                new Event()
                {
                    Id = 2,
                    ChargerId = 1, // Charger Dorm
                    CardId = 2, // User's card
                    StartedAt = DateTime.Now.AddHours(-1),
                    EndedAt = DateTime.Now.AddMinutes(40),
                    VolumeKwh = 7.2,
                },
                new Event()
                {
                    Id = 3,
                    ChargerId = 2, // Charger Mobilisis
                    CardId = 2, // User's card
                    StartedAt = DateTime.Now.AddHours(-2),
                    EndedAt = DateTime.Now.AddMinutes(35),
                    VolumeKwh = 5.1,
                },
                new Event()
                {
                    Id = 4,
                    ChargerId = 2, // Charger Mobilisis
                    CardId = 4, // John Doe's card
                    StartedAt = DateTime.Now.AddHours(-1),
                    EndedAt = DateTime.Now.AddMinutes(45),
                    VolumeKwh = 14.6,
                },
                new Event()
                {
                    Id = 5,
                    ChargerId = 3, // Charger FOI
                    CardId = 4, // John Doe's card
                    StartedAt = DateTime.Now.AddHours(-3),
                    EndedAt = DateTime.Now.AddMinutes(55),
                    VolumeKwh = 11.3,
                },
                new Event()
                {
                    Id = 6,
                    ChargerId = 1, // Charger Dorm
                    CardId = 1, // Admin's card
                    StartedAt = DateTime.Now.AddHours(-3),
                    EndedAt = DateTime.Now.AddMinutes(25),
                    VolumeKwh = 7.5,
                },
                new Event()
                {
                    Id = 7,
                    ChargerId = 2, // Charger Mobilisis
                    CardId = 1, // Admin's card
                    StartedAt = DateTime.Now.AddHours(-4),
                    EndedAt = DateTime.Now.AddMinutes(40),
                    VolumeKwh = 8.9,
                },
                new Event()
                {
                    Id = 8,
                    ChargerId = 3, // Charger FOI
                    CardId = 3, // User's card
                    StartedAt = DateTime.Now.AddHours(-2),
                    EndedAt = DateTime.Now.AddMinutes(50),
                    VolumeKwh = 10.2,
                }
            );
        }
    }
}
