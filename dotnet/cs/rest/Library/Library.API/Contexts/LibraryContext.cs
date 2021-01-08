using Library.API.Entities;
using Microsoft.EntityFrameworkCore;
using System;

namespace Library.API.Contexts
{
    public class LibraryContext : DbContext
    {
        public DbSet<Book> Books { get; set; }

        public DbSet<Author> Authors { get; set; }

        public LibraryContext(DbContextOptions<LibraryContext> options)
            : base(options)
        {
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            // seed the database with dummy data
            modelBuilder.Entity<Author>().HasData(
                new Author()
                {
                    Id = Guid.Parse("d28888e9-2ba9-473a-a40f-e38cb54f9b35"),
                    FirstName = "George",
                    LastName = "RR Martin"
                },
                new Author()
                {
                    Id = Guid.Parse("da2fd609-d754-4feb-8acd-c4f9ff13ba96"),
                    FirstName = "Stephen",
                    LastName = "Fry"
                },
                new Author()
                {
                    Id = Guid.Parse("24810dfc-2d94-4cc7-aab5-cdf98b83f0c9"),
                    FirstName = "James",
                    LastName = "Elroy"
                },
                new Author()
                {
                    Id = Guid.Parse("2902b665-1190-4c70-9915-b9c2d7680450"),
                    FirstName = "Douglas",
                    LastName = "Adams"
                }
                );

            // seed the database with dummy data
            modelBuilder.Entity<Book>().HasData(
                new Book
                {
                    Id = Guid.Parse("5b1c2b4d-48c7-402a-80c3-cc796ad49c6b"),
                    AuthorId = Guid.Parse("d28888e9-2ba9-473a-a40f-e38cb54f9b35"),
                    Title = "A Dance with Dragons",
                    Description = "A Dance with Dragons is the fifth of seven planned novels in the epic fantasy series A Song of Ice and Fire."
                },
                new Book
                {
                    Id = Guid.Parse("d8663e5e-7494-4f81-8739-6e0de1bea7ee"),
                    AuthorId = Guid.Parse("d28888e9-2ba9-473a-a40f-e38cb54f9b35"),
                    Title = "A Game of Thrones",
                    Description = "A Game of Thrones is the first novel in A Song of Ice and Fire, a series of fantasy novels by American author George R. R. ... In the novel, recounting events from various points of view, Martin introduces the plot-lines of the noble houses of Westeros, the Wall, and the Targaryens."
                },
                new Book
                {
                    Id = Guid.Parse("d173e20d-159e-4127-9ce9-b0ac2564ad97"),
                    AuthorId = Guid.Parse("da2fd609-d754-4feb-8acd-c4f9ff13ba96"),
                    Title = "Mythos",
                    Description = "The Greek myths are amongst the best stories ever told, passed down through millennia and inspiring writers and artists as varied as Shakespeare, Michelangelo, James Joyce and Walt Disney.  They are embedded deeply in the traditions, tales and cultural DNA of the West.You'll fall in love with Zeus, marvel at the birth of Athena, wince at Cronus and Gaia's revenge on Ouranos, weep with King Midas and hunt with the beautiful and ferocious Artemis. Spellbinding, informative and moving, Stephen Fry's Mythos perfectly captures these stories for the modern age - in all their rich and deeply human relevance."
                },
                new Book
                {
                    Id = Guid.Parse("493c3228-3444-4a49-9cc0-e8532edc59b2"),
                    AuthorId = Guid.Parse("24810dfc-2d94-4cc7-aab5-cdf98b83f0c9"),
                    Title = "American Tabloid",
                    Description = "American Tabloid is a 1995 novel by James Ellroy that chronicles the events surrounding three rogue American law enforcement officers from November 22, 1958 through November 22, 1963. Each becomes entangled in a web of interconnecting associations between the FBI, the CIA, and the mafia, which eventually leads to their collective involvement in the John F. Kennedy assassination."
                },
                new Book
                {
                    Id = Guid.Parse("40ff5488-fdab-45b5-bc3a-14302d59869a"),
                    AuthorId = Guid.Parse("2902b665-1190-4c70-9915-b9c2d7680450"),
                    Title = "The Hitchhiker's Guide to the Galaxy",
                    Description = "In The Hitchhiker's Guide to the Galaxy, the characters visit the legendary planet Magrathea, home to the now-collapsed planet-building industry, and meet Slartibartfast, a planetary coastline designer who was responsible for the fjords of Norway. Through archival recordings, he relates the story of a race of hyper-intelligent pan-dimensional beings who built a computer named Deep Thought to calculate the Answer to the Ultimate Question of Life, the Universe, and Everything."
                }
                );

            base.OnModelCreating(modelBuilder);
        }
    }
}
