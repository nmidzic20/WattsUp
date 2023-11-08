using System;
using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace backend.Data.Migrations
{
    /// <inheritdoc />
    public partial class AddRefreshTokenMigration : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "RefreshTokenId",
                table: "User",
                type: "integer",
                nullable: true);

            migrationBuilder.CreateTable(
                name: "RefreshToken",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    Token = table.Column<string>(type: "text", nullable: false),
                    ExpiresAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_RefreshToken", x => x.Id);
                });

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

            migrationBuilder.InsertData(
                table: "RefreshToken",
                columns: new[] { "Id", "ExpiresAt", "Token" },
                values: new object[] { 1, new DateTime(2023, 11, 9, 14, 41, 33, 517, DateTimeKind.Utc).AddTicks(1763), "0x1A2B3C4F5D6EAES3DF4FFDE4" });

            migrationBuilder.UpdateData(
                table: "User",
                keyColumn: "Id",
                keyValue: 1,
                columns: new[] { "CreatedAt", "Password", "RefreshTokenId" },
                values: new object[] { new DateTime(2023, 11, 8, 14, 41, 33, 517, DateTimeKind.Utc).AddTicks(1799), "AQAAAAIAAYagAAAAEPEKcsvV0vlnV44MA9nUyjPxBA1lJUXrsJfs5R6SKleD44BeLLXV30oa/F3+zLx7Eg==", null });

            migrationBuilder.UpdateData(
                table: "User",
                keyColumn: "Id",
                keyValue: 2,
                columns: new[] { "CreatedAt", "Password", "RefreshTokenId" },
                values: new object[] { new DateTime(2023, 11, 8, 15, 41, 33, 631, DateTimeKind.Utc).AddTicks(9182), "AQAAAAIAAYagAAAAEFlWiar2dxRyzl26eF7iS9qeHnMB20Z6JAL+sM9sJHX8uHoA+jivQ+mqW0RVsCmGbQ==", null });

            migrationBuilder.UpdateData(
                table: "User",
                keyColumn: "Id",
                keyValue: 3,
                columns: new[] { "CreatedAt", "Password", "RefreshTokenId" },
                values: new object[] { new DateTime(2023, 11, 8, 16, 41, 33, 750, DateTimeKind.Utc).AddTicks(4619), "AQAAAAIAAYagAAAAEE0LVQ5mGf8bCCIJ/r3vv4TD3w+MkMgw+wXZIYveI7zwcBJWWdFs9AmwNwOlHpcfNw==", 1 });

            migrationBuilder.CreateIndex(
                name: "IX_User_RefreshTokenId",
                table: "User",
                column: "RefreshTokenId");

            migrationBuilder.AddForeignKey(
                name: "FK_User_RefreshToken_RefreshTokenId",
                table: "User",
                column: "RefreshTokenId",
                principalTable: "RefreshToken",
                principalColumn: "Id");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_User_RefreshToken_RefreshTokenId",
                table: "User");

            migrationBuilder.DropTable(
                name: "RefreshToken");

            migrationBuilder.DropIndex(
                name: "IX_User_RefreshTokenId",
                table: "User");

            migrationBuilder.DropColumn(
                name: "RefreshTokenId",
                table: "User");

            migrationBuilder.UpdateData(
                table: "Charger",
                keyColumn: "Id",
                keyValue: 1,
                columns: new[] { "CreatedAt", "LastSyncAt" },
                values: new object[] { new DateTime(2023, 10, 28, 0, 19, 33, 945, DateTimeKind.Local).AddTicks(4795), new DateTime(2023, 10, 28, 1, 19, 33, 945, DateTimeKind.Local).AddTicks(4799) });

            migrationBuilder.UpdateData(
                table: "Charger",
                keyColumn: "Id",
                keyValue: 2,
                columns: new[] { "CreatedAt", "LastSyncAt" },
                values: new object[] { new DateTime(2023, 10, 28, 0, 19, 33, 945, DateTimeKind.Local).AddTicks(4804), new DateTime(2023, 10, 28, 2, 19, 33, 945, DateTimeKind.Local).AddTicks(4807) });

            migrationBuilder.UpdateData(
                table: "Charger",
                keyColumn: "Id",
                keyValue: 3,
                columns: new[] { "CreatedAt", "LastSyncAt" },
                values: new object[] { new DateTime(2023, 10, 28, 0, 19, 33, 945, DateTimeKind.Local).AddTicks(4811), new DateTime(2023, 10, 28, 3, 19, 33, 945, DateTimeKind.Local).AddTicks(4814) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 1,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 10, 28, 0, 49, 33, 945, DateTimeKind.Local).AddTicks(4845), new DateTime(2023, 10, 27, 22, 19, 33, 945, DateTimeKind.Local).AddTicks(4841) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 2,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 10, 28, 0, 59, 33, 945, DateTimeKind.Local).AddTicks(4853), new DateTime(2023, 10, 27, 23, 19, 33, 945, DateTimeKind.Local).AddTicks(4850) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 3,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 10, 28, 0, 54, 33, 945, DateTimeKind.Local).AddTicks(4859), new DateTime(2023, 10, 27, 22, 19, 33, 945, DateTimeKind.Local).AddTicks(4856) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 4,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 10, 28, 1, 4, 33, 945, DateTimeKind.Local).AddTicks(4866), new DateTime(2023, 10, 27, 23, 19, 33, 945, DateTimeKind.Local).AddTicks(4863) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 5,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 10, 28, 1, 14, 33, 945, DateTimeKind.Local).AddTicks(4873), new DateTime(2023, 10, 27, 21, 19, 33, 945, DateTimeKind.Local).AddTicks(4870) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 6,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 10, 28, 0, 44, 33, 945, DateTimeKind.Local).AddTicks(4879), new DateTime(2023, 10, 27, 21, 19, 33, 945, DateTimeKind.Local).AddTicks(4876) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 7,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 10, 28, 0, 59, 33, 945, DateTimeKind.Local).AddTicks(4886), new DateTime(2023, 10, 27, 20, 19, 33, 945, DateTimeKind.Local).AddTicks(4883) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 8,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 10, 28, 1, 9, 33, 945, DateTimeKind.Local).AddTicks(4893), new DateTime(2023, 10, 27, 22, 19, 33, 945, DateTimeKind.Local).AddTicks(4890) });

            migrationBuilder.UpdateData(
                table: "User",
                keyColumn: "Id",
                keyValue: 1,
                columns: new[] { "CreatedAt", "Password" },
                values: new object[] { new DateTime(2023, 10, 28, 0, 19, 33, 945, DateTimeKind.Local).AddTicks(4628), "123456" });

            migrationBuilder.UpdateData(
                table: "User",
                keyColumn: "Id",
                keyValue: 2,
                columns: new[] { "CreatedAt", "Password" },
                values: new object[] { new DateTime(2023, 10, 28, 1, 19, 33, 945, DateTimeKind.Local).AddTicks(4686), "123456" });

            migrationBuilder.UpdateData(
                table: "User",
                keyColumn: "Id",
                keyValue: 3,
                columns: new[] { "CreatedAt", "Password" },
                values: new object[] { new DateTime(2023, 10, 28, 2, 19, 33, 945, DateTimeKind.Local).AddTicks(4692), "123456" });
        }
    }
}
