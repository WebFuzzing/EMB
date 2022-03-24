import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Jimmy Jia",
  about:
    "Jimmy is a principal scientist and full-stack developer at Butterfly Network and 4Catalyzer. He is an advocate for GraphQL internally, and is committed to using machine learning and the web to save lives by advancing healthcare technology. He also maintains a number of open-source libraries within the GraphQL ecosystem.",
  image: {
    url: "people/jimmy.jpg",
  },
  social: {
    homepage: "http://www.fashionablenonsense.com/",
    twitter: "jimmy_jia",
    github: "taion",
  },
  location: {
    country: {
      name: "USA",
      code: "US",
    },
    city: "New York",
  },
  keywords: [Keyword.GRAPHQL],
  type: [ContactType.SPEAKER, ContactType.TALK],
};

export default person;
