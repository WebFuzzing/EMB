using System;

namespace Library.API.Models
{
    public class BookWithConcatenatedAuthorName
    {
        public Guid Id { get; set; }

        public string Author { get; set; }

        public string Title { get; set; }

        public string Description { get; set; }
    }
}
