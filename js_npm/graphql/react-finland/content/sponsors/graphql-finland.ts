import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "GraphQL Finland",
  about: "Learn More about GraphQL, Explore Finland",
  aboutShort: "Learn More about GraphQL, Explore Finland",
  image: {
    url: "graphql-finland/logo/v1/logo-colored-with-text.svg",
  },
  social: {
    homepage: "https://www.graphql-finland.fi/",
    github: "GraphQLFinland",
    medium: "GraphQLFinland",
    twitter: "GraphQLFinland",
    // TODO: Replace once we have a proper name
    youtube: "UCReJkc0nO9jhVTNgm5psuFA",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "Helsinki",
  },
  type: [ContactType.ORGANIZER],
};

export default sponsor;
