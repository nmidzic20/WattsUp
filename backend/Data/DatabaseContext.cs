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

    }
}
