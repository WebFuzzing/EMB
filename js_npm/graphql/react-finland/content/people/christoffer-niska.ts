import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Christoffer Niska",
  about:
    "Tech leader with a passion for programming. Head of Tech at @digiaonline. Father of four. Open Source enthusiast. JavaScript Ninja. GraphQL evangelist.",
  image: {
    url: "people/crisu.jpg",
  },
  social: {
    homepage: "https://about.me/crisu83",
    twitter: "Crisu83",
    github: "crisu83",
    linkedin: "crisu83",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "Helsinki",
  },
  keywords: [Keyword.GRAPHQL],
  type: [ContactType.SPEAKER],
};

export default person;
