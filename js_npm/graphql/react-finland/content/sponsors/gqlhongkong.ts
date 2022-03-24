import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "GraphQL Hong Kong",
  about: "GraphQL at Hong Kong",
  image: {
    url: "sponsors/gqlhongkong.png",
  },
  social: {
    homepage: "https://graphql-hongkong.org/",
    twitter: "GraphQLHongKong",
  },
  location: {
    country: {
      name: "Hong Kong",
      code: "HK",
    },
    city: "Hong Kong",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
