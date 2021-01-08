using System;
using Microsoft.EntityFrameworkCore.Migrations;

namespace Library.API.Migrations
{
    public partial class InitialMigration : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "Authors",
                columns: table => new
                {
                    Id = table.Column<Guid>(nullable: false),
                    FirstName = table.Column<string>(maxLength: 150, nullable: false),
                    LastName = table.Column<string>(maxLength: 150, nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Authors", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Books",
                columns: table => new
                {
                    Id = table.Column<Guid>(nullable: false),
                    Title = table.Column<string>(maxLength: 150, nullable: false),
                    Description = table.Column<string>(maxLength: 2500, nullable: true),
                    AmountOfPages = table.Column<int>(nullable: true),
                    AuthorId = table.Column<Guid>(nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Books", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Books_Authors_AuthorId",
                        column: x => x.AuthorId,
                        principalTable: "Authors",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.InsertData(
                table: "Authors",
                columns: new[] { "Id", "FirstName", "LastName" },
                values: new object[,]
                {
                    { new Guid("d28888e9-2ba9-473a-a40f-e38cb54f9b35"), "George", "RR Martin" },
                    { new Guid("da2fd609-d754-4feb-8acd-c4f9ff13ba96"), "Stephen", "Fry" },
                    { new Guid("24810dfc-2d94-4cc7-aab5-cdf98b83f0c9"), "James", "Elroy" },
                    { new Guid("2902b665-1190-4c70-9915-b9c2d7680450"), "Douglas", "Adams" }
                });

            migrationBuilder.InsertData(
                table: "Books",
                columns: new[] { "Id", "AmountOfPages", "AuthorId", "Description", "Title" },
                values: new object[,]
                {
                    { new Guid("5b1c2b4d-48c7-402a-80c3-cc796ad49c6b"), null, new Guid("d28888e9-2ba9-473a-a40f-e38cb54f9b35"), "A Dance with Dragons is the fifth of seven planned novels in the epic fantasy series A Song of Ice and Fire.", "A Dance with Dragons" },
                    { new Guid("d8663e5e-7494-4f81-8739-6e0de1bea7ee"), null, new Guid("d28888e9-2ba9-473a-a40f-e38cb54f9b35"), "A Game of Thrones is the first novel in A Song of Ice and Fire, a series of fantasy novels by American author George R. R. ... In the novel, recounting events from various points of view, Martin introduces the plot-lines of the noble houses of Westeros, the Wall, and the Targaryens.", "A Game of Thrones" },
                    { new Guid("d173e20d-159e-4127-9ce9-b0ac2564ad97"), null, new Guid("da2fd609-d754-4feb-8acd-c4f9ff13ba96"), "The Greek myths are amongst the best stories ever told, passed down through millennia and inspiring writers and artists as varied as Shakespeare, Michelangelo, James Joyce and Walt Disney.  They are embedded deeply in the traditions, tales and cultural DNA of the West.You'll fall in love with Zeus, marvel at the birth of Athena, wince at Cronus and Gaia's revenge on Ouranos, weep with King Midas and hunt with the beautiful and ferocious Artemis. Spellbinding, informative and moving, Stephen Fry's Mythos perfectly captures these stories for the modern age - in all their rich and deeply human relevance.", "Mythos" },
                    { new Guid("493c3228-3444-4a49-9cc0-e8532edc59b2"), null, new Guid("24810dfc-2d94-4cc7-aab5-cdf98b83f0c9"), "American Tabloid is a 1995 novel by James Ellroy that chronicles the events surrounding three rogue American law enforcement officers from November 22, 1958 through November 22, 1963. Each becomes entangled in a web of interconnecting associations between the FBI, the CIA, and the mafia, which eventually leads to their collective involvement in the John F. Kennedy assassination.", "American Tabloid" },
                    { new Guid("40ff5488-fdab-45b5-bc3a-14302d59869a"), null, new Guid("2902b665-1190-4c70-9915-b9c2d7680450"), "In The Hitchhiker's Guide to the Galaxy, the characters visit the legendary planet Magrathea, home to the now-collapsed planet-building industry, and meet Slartibartfast, a planetary coastline designer who was responsible for the fjords of Norway. Through archival recordings, he relates the story of a race of hyper-intelligent pan-dimensional beings who built a computer named Deep Thought to calculate the Answer to the Ultimate Question of Life, the Universe, and Everything.", "The Hitchhiker's Guide to the Galaxy" }
                });

            migrationBuilder.CreateIndex(
                name: "IX_Books_AuthorId",
                table: "Books",
                column: "AuthorId");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "Books");

            migrationBuilder.DropTable(
                name: "Authors");
        }
    }
}
