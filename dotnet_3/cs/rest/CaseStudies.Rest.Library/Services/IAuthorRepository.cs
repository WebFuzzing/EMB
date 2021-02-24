using CaseStudies.Rest.Library.Entities;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace CaseStudies.Rest.Library.Services
{
    public interface IAuthorRepository : IDisposable
    {
        Task<bool> AuthorExistsAsync(Guid authorId);

        Task<IEnumerable<Author>> GetAuthorsAsync();

        Task<Author> GetAuthorAsync(Guid authorId);

        void UpdateAuthor(Author author);

        Task<bool> SaveChangesAsync();
    }
}
