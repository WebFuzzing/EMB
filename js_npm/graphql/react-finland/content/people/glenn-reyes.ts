import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Glenn Reyes",
  about:
    "Glenn is a front-end Engineer, who builds modern user interfaces and apps with React & GraphQL, loves OSS, co-organizes ReactVienna meetups, and plays JM music on guitar.",
  image: {
    url: "people/glenn.jpg",
  },
  social: {
    homepage: "https://glennreyes.com/",
    twitter: "glnnrys",
    github: "glennreyes",
  },
  location: {
    country: {
      name: "Austria",
      code: "AT",
    },
    city: "Vienna",
  },
  keywords: [Keyword.GRAPHQL, Keyword.GRAPHQLJS],
  type: [ContactType.SPEAKER],
};

export default person;
