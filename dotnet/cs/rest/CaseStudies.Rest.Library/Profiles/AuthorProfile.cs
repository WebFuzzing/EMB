using AutoMapper;

namespace CaseStudies.Rest.Library.Profiles
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
