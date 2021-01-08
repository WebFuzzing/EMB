using AutoMapper;

namespace Library.API.Profiles
{
    public class AuthorProfile : Profile
    {
        public AuthorProfile()
        {
            CreateMap<Entities.Author, Models.Author>();
            
            CreateMap<Models.AuthorForUpdate, Entities.Author>();
        }
    }
}
