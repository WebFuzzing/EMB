using Library.API.Entities;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Library.API.Services
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
