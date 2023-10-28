using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

#pragma warning disable CA1814 // Prefer jagged arrays over multidimensional

namespace backend.Data.Migrations
{
    /// <inheritdoc />
    public partial class AddMockDataMigration : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.InsertData(
                table: "Role",
                columns: new[] { "Id", "Name" },
                values: new object[,]
                {
                    { 1, "Admin" },
                    { 2, "User" }
                });

            migrationBuilder.InsertData(
                table: "User",
                columns: new[] { "Id", "Active", "CreatedAt", "Email", "FirstName", "LastName", "Password", "RoleId" },
                values: new object[,]
                {
                    { 1, true, new DateTime(2023, 10, 28, 0, 19, 33, 945, DateTimeKind.Utc).AddTicks(4628), "admin@gmail.com", "Admin", "Admin", "123456", 1 },
                    { 2, true, new DateTime(2023, 10, 28, 1, 19, 33, 945, DateTimeKind.Utc).AddTicks(4686), "user@gmail.com", "User", "User", "123456", 2 },
                    { 3, true, new DateTime(2023, 10, 28, 2, 19, 33, 945, DateTimeKind.Utc).AddTicks(4692), "john.doe@gmail.com", "John", "Doe", "123456", 2 }
                });

            migrationBuilder.InsertData(
                table: "Card",
                columns: new[] { "Id", "Active", "OwnedById", "Value" },
                values: new object[,]
                {
                    { 1, true, 1, "0x1A2B3C4F5D6E" },
                    { 2, true, 2, "0x4F5D6E1A2B3C" },
                    { 3, true, 2, "0x7F8A9D4F5D6E" },
                    { 4, true, 3, "0x7F8A9D1A2B3C" }
                });

            migrationBuilder.InsertData(
                table: "Charger",
                columns: new[] { "Id", "Active", "CreatedAt", "CreatedById", "LastSyncAt", "Latitude", "Longitude", "Name" },
                values: new object[,]
                {
                    { 1, true, new DateTime(2023, 10, 28, 0, 19, 33, 945, DateTimeKind.Utc).AddTicks(4795), 1, new DateTime(2023, 10, 28, 1, 19, 33, 945, DateTimeKind.Utc).AddTicks(4799), 46.309735000000003, 16.348593000000001, "Charger Dorm" },
                    { 2, true, new DateTime(2023, 10, 28, 0, 19, 33, 945, DateTimeKind.Utc).AddTicks(4804), 1, new DateTime(2023, 10, 28, 2, 19, 33, 945, DateTimeKind.Utc).AddTicks(4807), 46.287309, 16.321732999999998, "Charger Mobilisis" },
                    { 3, true, new DateTime(2023, 10, 28, 0, 19, 33, 945, DateTimeKind.Utc).AddTicks(4811), 1, new DateTime(2023, 10, 28, 3, 19, 33, 945, DateTimeKind.Utc).AddTicks(4814), 46.307789999999997, 16.338061, "Charger FOI" }
                });

            migrationBuilder.InsertData(
                table: "Event",
                columns: new[] { "Id", "CardId", "ChargerId", "EndedAt", "StartedAt", "VolumeKwh" },
                values: new object[,]
                {
                    { 1, 1, 1, new DateTime(2023, 10, 28, 0, 49, 33, 945, DateTimeKind.Utc).AddTicks(4845), new DateTime(2023, 10, 27, 22, 19, 33, 945, DateTimeKind.Utc).AddTicks(4841), 9.8000000000000007 },
                    { 2, 2, 1, new DateTime(2023, 10, 28, 0, 59, 33, 945, DateTimeKind.Utc).AddTicks(4853), new DateTime(2023, 10, 27, 23, 19, 33, 945, DateTimeKind.Utc).AddTicks(4850), 7.2000000000000002 },
                    { 3, 2, 2, new DateTime(2023, 10, 28, 0, 54, 33, 945, DateTimeKind.Utc).AddTicks(4859), new DateTime(2023, 10, 27, 22, 19, 33, 945, DateTimeKind.Utc).AddTicks(4856), 5.0999999999999996 },
                    { 4, 4, 2, new DateTime(2023, 10, 28, 1, 4, 33, 945, DateTimeKind.Utc).AddTicks(4866), new DateTime(2023, 10, 27, 23, 19, 33, 945, DateTimeKind.Utc).AddTicks(4863), 14.6 },
                    { 5, 4, 3, new DateTime(2023, 10, 28, 1, 14, 33, 945, DateTimeKind.Utc).AddTicks(4873), new DateTime(2023, 10, 27, 21, 19, 33, 945, DateTimeKind.Utc).AddTicks(4870), 11.300000000000001 },
                    { 6, 1, 1, new DateTime(2023, 10, 28, 0, 44, 33, 945, DateTimeKind.Utc).AddTicks(4879), new DateTime(2023, 10, 27, 21, 19, 33, 945, DateTimeKind.Utc).AddTicks(4876), 7.5 },
                    { 7, 1, 2, new DateTime(2023, 10, 28, 0, 59, 33, 945, DateTimeKind.Utc).AddTicks(4886), new DateTime(2023, 10, 27, 20, 19, 33, 945, DateTimeKind.Utc).AddTicks(4883), 8.9000000000000004 },
                    { 8, 3, 3, new DateTime(2023, 10, 28, 1, 9, 33, 945, DateTimeKind.Utc).AddTicks(4893), new DateTime(2023, 10, 27, 22, 19, 33, 945, DateTimeKind.Utc).AddTicks(4890), 10.199999999999999 }
                });
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DeleteData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 1);

            migrationBuilder.DeleteData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 2);

            migrationBuilder.DeleteData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 3);

            migrationBuilder.DeleteData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 4);

            migrationBuilder.DeleteData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 5);

            migrationBuilder.DeleteData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 6);

            migrationBuilder.DeleteData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 7);

            migrationBuilder.DeleteData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 8);

            migrationBuilder.DeleteData(
                table: "Card",
                keyColumn: "Id",
                keyValue: 1);

            migrationBuilder.DeleteData(
                table: "Card",
                keyColumn: "Id",
                keyValue: 2);

            migrationBuilder.DeleteData(
                table: "Card",
                keyColumn: "Id",
                keyValue: 3);

            migrationBuilder.DeleteData(
                table: "Card",
                keyColumn: "Id",
                keyValue: 4);

            migrationBuilder.DeleteData(
                table: "Charger",
                keyColumn: "Id",
                keyValue: 1);

            migrationBuilder.DeleteData(
                table: "Charger",
                keyColumn: "Id",
                keyValue: 2);

            migrationBuilder.DeleteData(
                table: "Charger",
                keyColumn: "Id",
                keyValue: 3);

            migrationBuilder.DeleteData(
                table: "User",
                keyColumn: "Id",
                keyValue: 1);

            migrationBuilder.DeleteData(
                table: "User",
                keyColumn: "Id",
                keyValue: 2);

            migrationBuilder.DeleteData(
                table: "User",
                keyColumn: "Id",
                keyValue: 3);

            migrationBuilder.DeleteData(
                table: "Role",
                keyColumn: "Id",
                keyValue: 1);

            migrationBuilder.DeleteData(
                table: "Role",
                keyColumn: "Id",
                keyValue: 2);
        }
    }
}
