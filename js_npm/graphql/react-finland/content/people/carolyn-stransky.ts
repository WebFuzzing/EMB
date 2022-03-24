import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Carolyn Stransky",
  about:
    "Carolyn Stransky (she/her) is a software developer and journalist based in Berlin, Germany. Her work focuses on creating accessible, user-centric interfaces. Out of the office, she tries to convince everyone that documentation is cool while also maintaining an admittedly ridiculous skincare routine. You can find her most places on the Internet @carolstran.",
  image: {
    url: "people/carolyn.jpg",
  },
  social: {
    twitter: "carolstran",
    github: "carolstran",
  },
  location: {
    country: {
      name: "Germany",
      code: "DE",
    },
    city: "Berlin",
  },
  keywords: [Keyword.GRAPHQL, Keyword.GRAPHQLJS],
  type: [ContactType.SPEAKER],
};

export default person;
