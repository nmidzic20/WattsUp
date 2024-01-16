using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace backend.Data.Migrations
{
    /// <inheritdoc />
    public partial class AddPasswordResetToken : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<string>(
                name: "PasswordResetToken",
                table: "User",
                type: "text",
                nullable: true);

            migrationBuilder.UpdateData(
                table: "Charger",
                keyColumn: "Id",
                keyValue: 1,
                columns: new[] { "CreatedAt", "LastSyncAt" },
                values: new object[] { new DateTime(2024, 1, 16, 13, 17, 0, 849, DateTimeKind.Utc).AddTicks(2770), new DateTime(2024, 1, 16, 14, 17, 0, 849, DateTimeKind.Utc).AddTicks(2773) });

            migrationBuilder.UpdateData(
                table: "Charger",
                keyColumn: "Id",
                keyValue: 2,
                columns: new[] { "CreatedAt", "LastSyncAt" },
                values: new object[] { new DateTime(2024, 1, 16, 13, 17, 0, 849, DateTimeKind.Utc).AddTicks(2781), new DateTime(2024, 1, 16, 15, 17, 0, 849, DateTimeKind.Utc).AddTicks(2782) });

            migrationBuilder.UpdateData(
                table: "Charger",
                keyColumn: "Id",
                keyValue: 3,
                columns: new[] { "CreatedAt", "LastSyncAt" },
                values: new object[] { new DateTime(2024, 1, 16, 13, 17, 0, 849, DateTimeKind.Utc).AddTicks(2784), new DateTime(2024, 1, 16, 16, 17, 0, 849, DateTimeKind.Utc).AddTicks(2785) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 1,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2024, 1, 16, 13, 47, 0, 849, DateTimeKind.Utc).AddTicks(2812), new DateTime(2024, 1, 16, 11, 17, 0, 849, DateTimeKind.Utc).AddTicks(2811) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 2,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2024, 1, 16, 13, 57, 0, 849, DateTimeKind.Utc).AddTicks(2851), new DateTime(2024, 1, 16, 12, 17, 0, 849, DateTimeKind.Utc).AddTicks(2850) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 3,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2024, 1, 16, 13, 52, 0, 849, DateTimeKind.Utc).AddTicks(2859), new DateTime(2024, 1, 16, 11, 17, 0, 849, DateTimeKind.Utc).AddTicks(2853) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 4,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2024, 1, 16, 14, 2, 0, 849, DateTimeKind.Utc).AddTicks(2862), new DateTime(2024, 1, 16, 12, 17, 0, 849, DateTimeKind.Utc).AddTicks(2861) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 5,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2024, 1, 16, 14, 12, 0, 849, DateTimeKind.Utc).AddTicks(2865), new DateTime(2024, 1, 16, 10, 17, 0, 849, DateTimeKind.Utc).AddTicks(2864) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 6,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2024, 1, 16, 13, 42, 0, 849, DateTimeKind.Utc).AddTicks(2868), new DateTime(2024, 1, 16, 10, 17, 0, 849, DateTimeKind.Utc).AddTicks(2867) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 7,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2024, 1, 16, 13, 57, 0, 849, DateTimeKind.Utc).AddTicks(2871), new DateTime(2024, 1, 16, 9, 17, 0, 849, DateTimeKind.Utc).AddTicks(2870) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 8,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2024, 1, 16, 14, 7, 0, 849, DateTimeKind.Utc).AddTicks(2874), new DateTime(2024, 1, 16, 11, 17, 0, 849, DateTimeKind.Utc).AddTicks(2873) });

            migrationBuilder.UpdateData(
                table: "RefreshToken",
                keyColumn: "Id",
                keyValue: 1,
                column: "ExpiresAt",
                value: new DateTime(2024, 1, 17, 13, 17, 0, 465, DateTimeKind.Utc).AddTicks(4996));

            migrationBuilder.UpdateData(
                table: "User",
                keyColumn: "Id",
                keyValue: 1,
                columns: new[] { "CreatedAt", "Password", "PasswordResetToken" },
                values: new object[] { new DateTime(2024, 1, 16, 13, 17, 0, 465, DateTimeKind.Utc).AddTicks(5064), "AQAAAAIAAYagAAAAEHlPdltkW8LHEGsTfnQ3uRbZFZ7J2Qtx5n05HPU67U7cOwPW/FTOBFx8Qab1jycxWw==", null });

            migrationBuilder.UpdateData(
                table: "User",
                keyColumn: "Id",
                keyValue: 2,
                columns: new[] { "CreatedAt", "Password", "PasswordResetToken" },
                values: new object[] { new DateTime(2024, 1, 16, 14, 17, 0, 626, DateTimeKind.Utc).AddTicks(4746), "AQAAAAIAAYagAAAAECmojiKIGq5sbaqNFRNg7A2YZsZO6nKnUoMkMGeVHz+TqVeKIWUYRA9BFLBrRPkA9Q==", null });

            migrationBuilder.UpdateData(
                table: "User",
                keyColumn: "Id",
                keyValue: 3,
                columns: new[] { "CreatedAt", "Password", "PasswordResetToken" },
                values: new object[] { new DateTime(2024, 1, 16, 15, 17, 0, 737, DateTimeKind.Utc).AddTicks(8973), "AQAAAAIAAYagAAAAENrmidWnL8zmtpmR9UeZ0L9XkH7krHOWUS5QPXIoZ977GqWHnLFI06deH/Me10GS5w==", null });
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "PasswordResetToken",
                table: "User");

            migrationBuilder.UpdateData(
                table: "Charger",
                keyColumn: "Id",
                keyValue: 1,
                columns: new[] { "CreatedAt", "LastSyncAt" },
                values: new object[] { new DateTime(2023, 11, 8, 14, 41, 33, 861, DateTimeKind.Utc).AddTicks(7596), new DateTime(2023, 11, 8, 15, 41, 33, 861, DateTimeKind.Utc).AddTicks(7600) });

            migrationBuilder.UpdateData(
                table: "Charger",
                keyColumn: "Id",
                keyValue: 2,
                columns: new[] { "CreatedAt", "LastSyncAt" },
                values: new object[] { new DateTime(2023, 11, 8, 14, 41, 33, 861, DateTimeKind.Utc).AddTicks(7609), new DateTime(2023, 11, 8, 16, 41, 33, 861, DateTimeKind.Utc).AddTicks(7610) });

            migrationBuilder.UpdateData(
                table: "Charger",
                keyColumn: "Id",
                keyValue: 3,
                columns: new[] { "CreatedAt", "LastSyncAt" },
                values: new object[] { new DateTime(2023, 11, 8, 14, 41, 33, 861, DateTimeKind.Utc).AddTicks(7612), new DateTime(2023, 11, 8, 17, 41, 33, 861, DateTimeKind.Utc).AddTicks(7613) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 1,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 11, 8, 15, 11, 33, 861, DateTimeKind.Utc).AddTicks(7640), new DateTime(2023, 11, 8, 12, 41, 33, 861, DateTimeKind.Utc).AddTicks(7639) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 2,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 11, 8, 15, 21, 33, 861, DateTimeKind.Utc).AddTicks(7644), new DateTime(2023, 11, 8, 13, 41, 33, 861, DateTimeKind.Utc).AddTicks(7643) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 3,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 11, 8, 15, 16, 33, 861, DateTimeKind.Utc).AddTicks(7654), new DateTime(2023, 11, 8, 12, 41, 33, 861, DateTimeKind.Utc).AddTicks(7646) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 4,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 11, 8, 15, 26, 33, 861, DateTimeKind.Utc).AddTicks(7658), new DateTime(2023, 11, 8, 13, 41, 33, 861, DateTimeKind.Utc).AddTicks(7657) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 5,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 11, 8, 15, 36, 33, 861, DateTimeKind.Utc).AddTicks(7660), new DateTime(2023, 11, 8, 11, 41, 33, 861, DateTimeKind.Utc).AddTicks(7659) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 6,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 11, 8, 15, 6, 33, 861, DateTimeKind.Utc).AddTicks(7856), new DateTime(2023, 11, 8, 11, 41, 33, 861, DateTimeKind.Utc).AddTicks(7855) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 7,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 11, 8, 15, 21, 33, 861, DateTimeKind.Utc).AddTicks(7860), new DateTime(2023, 11, 8, 10, 41, 33, 861, DateTimeKind.Utc).AddTicks(7859) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 8,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 11, 8, 15, 31, 33, 861, DateTimeKind.Utc).AddTicks(7863), new DateTime(2023, 11, 8, 12, 41, 33, 861, DateTimeKind.Utc).AddTicks(7862) });

            migrationBuilder.UpdateData(
                table: "RefreshToken",
                keyColumn: "Id",
                keyValue: 1,
                column: "ExpiresAt",
                value: new DateTime(2023, 11, 9, 14, 41, 33, 517, DateTimeKind.Utc).AddTicks(1763));

            migrationBuilder.UpdateData(
                table: "User",
                keyColumn: "Id",
                keyValue: 1,
                columns: new[] { "CreatedAt", "Password" },
                values: new object[] { new DateTime(2023, 11, 8, 14, 41, 33, 517, DateTimeKind.Utc).AddTicks(1799), "AQAAAAIAAYagAAAAEPEKcsvV0vlnV44MA9nUyjPxBA1lJUXrsJfs5R6SKleD44BeLLXV30oa/F3+zLx7Eg==" });

            migrationBuilder.UpdateData(
                table: "User",
                keyColumn: "Id",
                keyValue: 2,
                columns: new[] { "CreatedAt", "Password" },
                values: new object[] { new DateTime(2023, 11, 8, 15, 41, 33, 631, DateTimeKind.Utc).AddTicks(9182), "AQAAAAIAAYagAAAAEFlWiar2dxRyzl26eF7iS9qeHnMB20Z6JAL+sM9sJHX8uHoA+jivQ+mqW0RVsCmGbQ==" });

            migrationBuilder.UpdateData(
                table: "User",
                keyColumn: "Id",
                keyValue: 3,
                columns: new[] { "CreatedAt", "Password" },
                values: new object[] { new DateTime(2023, 11, 8, 16, 41, 33, 750, DateTimeKind.Utc).AddTicks(4619), "AQAAAAIAAYagAAAAEE0LVQ5mGf8bCCIJ/r3vv4TD3w+MkMgw+wXZIYveI7zwcBJWWdFs9AmwNwOlHpcfNw==" });
        }
    }
}
