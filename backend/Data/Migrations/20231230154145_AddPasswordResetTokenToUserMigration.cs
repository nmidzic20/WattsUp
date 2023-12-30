using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace backend.Data.Migrations
{
    /// <inheritdoc />
    public partial class AddPasswordResetTokenToUserMigration : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.UpdateData(
                table: "Charger",
                keyColumn: "Id",
                keyValue: 1,
                columns: new[] { "CreatedAt", "LastSyncAt" },
                values: new object[] { new DateTime(2023, 12, 30, 15, 41, 45, 617, DateTimeKind.Utc).AddTicks(4993), new DateTime(2023, 12, 30, 16, 41, 45, 617, DateTimeKind.Utc).AddTicks(4996) });

            migrationBuilder.UpdateData(
                table: "Charger",
                keyColumn: "Id",
                keyValue: 2,
                columns: new[] { "CreatedAt", "LastSyncAt" },
                values: new object[] { new DateTime(2023, 12, 30, 15, 41, 45, 617, DateTimeKind.Utc).AddTicks(5003), new DateTime(2023, 12, 30, 17, 41, 45, 617, DateTimeKind.Utc).AddTicks(5004) });

            migrationBuilder.UpdateData(
                table: "Charger",
                keyColumn: "Id",
                keyValue: 3,
                columns: new[] { "CreatedAt", "LastSyncAt" },
                values: new object[] { new DateTime(2023, 12, 30, 15, 41, 45, 617, DateTimeKind.Utc).AddTicks(5006), new DateTime(2023, 12, 30, 18, 41, 45, 617, DateTimeKind.Utc).AddTicks(5006) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 1,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 12, 30, 16, 11, 45, 617, DateTimeKind.Utc).AddTicks(5035), new DateTime(2023, 12, 30, 13, 41, 45, 617, DateTimeKind.Utc).AddTicks(5034) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 2,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 12, 30, 16, 21, 45, 617, DateTimeKind.Utc).AddTicks(5037), new DateTime(2023, 12, 30, 14, 41, 45, 617, DateTimeKind.Utc).AddTicks(5036) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 3,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 12, 30, 16, 16, 45, 617, DateTimeKind.Utc).AddTicks(5158), new DateTime(2023, 12, 30, 13, 41, 45, 617, DateTimeKind.Utc).AddTicks(5158) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 4,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 12, 30, 16, 26, 45, 617, DateTimeKind.Utc).AddTicks(5160), new DateTime(2023, 12, 30, 14, 41, 45, 617, DateTimeKind.Utc).AddTicks(5160) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 5,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 12, 30, 16, 36, 45, 617, DateTimeKind.Utc).AddTicks(5162), new DateTime(2023, 12, 30, 12, 41, 45, 617, DateTimeKind.Utc).AddTicks(5162) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 6,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 12, 30, 16, 6, 45, 617, DateTimeKind.Utc).AddTicks(5164), new DateTime(2023, 12, 30, 12, 41, 45, 617, DateTimeKind.Utc).AddTicks(5163) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 7,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 12, 30, 16, 21, 45, 617, DateTimeKind.Utc).AddTicks(5166), new DateTime(2023, 12, 30, 11, 41, 45, 617, DateTimeKind.Utc).AddTicks(5165) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 8,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 12, 30, 16, 31, 45, 617, DateTimeKind.Utc).AddTicks(5167), new DateTime(2023, 12, 30, 13, 41, 45, 617, DateTimeKind.Utc).AddTicks(5167) });

            migrationBuilder.UpdateData(
                table: "RefreshToken",
                keyColumn: "Id",
                keyValue: 1,
                column: "ExpiresAt",
                value: new DateTime(2023, 12, 31, 15, 41, 45, 434, DateTimeKind.Utc).AddTicks(9673));

            migrationBuilder.UpdateData(
                table: "User",
                keyColumn: "Id",
                keyValue: 1,
                columns: new[] { "CreatedAt", "Password" },
                values: new object[] { new DateTime(2023, 12, 30, 15, 41, 45, 434, DateTimeKind.Utc).AddTicks(9692), "AQAAAAIAAYagAAAAEJjE2/NpiUFySqgSdGs/W+6jU7LhsMZOS/WMdB2fDGVofzwb+6a3Hlt6YvjVCpTQIA==" });

            migrationBuilder.UpdateData(
                table: "User",
                keyColumn: "Id",
                keyValue: 2,
                columns: new[] { "CreatedAt", "Password" },
                values: new object[] { new DateTime(2023, 12, 30, 16, 41, 45, 496, DateTimeKind.Utc).AddTicks(1972), "AQAAAAIAAYagAAAAEJGPR4geK2d5Mu99mexALH3+8ua8Q5D9OJn/mmh/7f6DUyKfcUw/wdVMo2k9aifa/w==" });

            migrationBuilder.UpdateData(
                table: "User",
                keyColumn: "Id",
                keyValue: 3,
                columns: new[] { "CreatedAt", "Password" },
                values: new object[] { new DateTime(2023, 12, 30, 17, 41, 45, 556, DateTimeKind.Utc).AddTicks(6480), "AQAAAAIAAYagAAAAEOrQZh52ppRplgha3Nc91Z33gE6YxO71ZeF1X98t2Quu40BzsMNncssOPAjDscD1IA==" });
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.UpdateData(
                table: "Charger",
                keyColumn: "Id",
                keyValue: 1,
                columns: new[] { "CreatedAt", "LastSyncAt" },
                values: new object[] { new DateTime(2023, 12, 30, 15, 35, 14, 612, DateTimeKind.Utc).AddTicks(5341), new DateTime(2023, 12, 30, 16, 35, 14, 612, DateTimeKind.Utc).AddTicks(5343) });

            migrationBuilder.UpdateData(
                table: "Charger",
                keyColumn: "Id",
                keyValue: 2,
                columns: new[] { "CreatedAt", "LastSyncAt" },
                values: new object[] { new DateTime(2023, 12, 30, 15, 35, 14, 612, DateTimeKind.Utc).AddTicks(5350), new DateTime(2023, 12, 30, 17, 35, 14, 612, DateTimeKind.Utc).AddTicks(5351) });

            migrationBuilder.UpdateData(
                table: "Charger",
                keyColumn: "Id",
                keyValue: 3,
                columns: new[] { "CreatedAt", "LastSyncAt" },
                values: new object[] { new DateTime(2023, 12, 30, 15, 35, 14, 612, DateTimeKind.Utc).AddTicks(5353), new DateTime(2023, 12, 30, 18, 35, 14, 612, DateTimeKind.Utc).AddTicks(5353) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 1,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 12, 30, 16, 5, 14, 612, DateTimeKind.Utc).AddTicks(5378), new DateTime(2023, 12, 30, 13, 35, 14, 612, DateTimeKind.Utc).AddTicks(5378) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 2,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 12, 30, 16, 15, 14, 612, DateTimeKind.Utc).AddTicks(5380), new DateTime(2023, 12, 30, 14, 35, 14, 612, DateTimeKind.Utc).AddTicks(5380) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 3,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 12, 30, 16, 10, 14, 612, DateTimeKind.Utc).AddTicks(5387), new DateTime(2023, 12, 30, 13, 35, 14, 612, DateTimeKind.Utc).AddTicks(5381) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 4,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 12, 30, 16, 20, 14, 612, DateTimeKind.Utc).AddTicks(5389), new DateTime(2023, 12, 30, 14, 35, 14, 612, DateTimeKind.Utc).AddTicks(5389) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 5,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 12, 30, 16, 30, 14, 612, DateTimeKind.Utc).AddTicks(5391), new DateTime(2023, 12, 30, 12, 35, 14, 612, DateTimeKind.Utc).AddTicks(5390) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 6,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 12, 30, 16, 0, 14, 612, DateTimeKind.Utc).AddTicks(5393), new DateTime(2023, 12, 30, 12, 35, 14, 612, DateTimeKind.Utc).AddTicks(5392) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 7,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 12, 30, 16, 15, 14, 612, DateTimeKind.Utc).AddTicks(5394), new DateTime(2023, 12, 30, 11, 35, 14, 612, DateTimeKind.Utc).AddTicks(5394) });

            migrationBuilder.UpdateData(
                table: "Event",
                keyColumn: "Id",
                keyValue: 8,
                columns: new[] { "EndedAt", "StartedAt" },
                values: new object[] { new DateTime(2023, 12, 30, 16, 25, 14, 612, DateTimeKind.Utc).AddTicks(5396), new DateTime(2023, 12, 30, 13, 35, 14, 612, DateTimeKind.Utc).AddTicks(5396) });

            migrationBuilder.UpdateData(
                table: "RefreshToken",
                keyColumn: "Id",
                keyValue: 1,
                column: "ExpiresAt",
                value: new DateTime(2023, 12, 31, 15, 35, 14, 433, DateTimeKind.Utc).AddTicks(8807));

            migrationBuilder.UpdateData(
                table: "User",
                keyColumn: "Id",
                keyValue: 1,
                columns: new[] { "CreatedAt", "Password" },
                values: new object[] { new DateTime(2023, 12, 30, 15, 35, 14, 433, DateTimeKind.Utc).AddTicks(8834), "AQAAAAIAAYagAAAAEB1E9GDeA2rZLf93QohsygWNvSOlpl5c71v6UV2bUaxG5ha2T488vOf4WLCFHJCKvA==" });

            migrationBuilder.UpdateData(
                table: "User",
                keyColumn: "Id",
                keyValue: 2,
                columns: new[] { "CreatedAt", "Password" },
                values: new object[] { new DateTime(2023, 12, 30, 16, 35, 14, 493, DateTimeKind.Utc).AddTicks(1008), "AQAAAAIAAYagAAAAEHi+bzQ7he04Je0jXFcNrWNTZslldqcqJUIKkUV93jMmqGexiioCzCxmgxLYGhAt2w==" });

            migrationBuilder.UpdateData(
                table: "User",
                keyColumn: "Id",
                keyValue: 3,
                columns: new[] { "CreatedAt", "Password" },
                values: new object[] { new DateTime(2023, 12, 30, 17, 35, 14, 552, DateTimeKind.Utc).AddTicks(7033), "AQAAAAIAAYagAAAAEOZT2Us6Z0kE9s5cpFNKoq1bhmiJUG2A0/vUti8ZYdvRFvnRnIyNYO5I5rpEMqHCtg==" });
        }
    }
}
