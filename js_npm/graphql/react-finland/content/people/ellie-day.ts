import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Ellie Day",
  about:
    "Ellie is a technologist, product engineer, speaker, teacher, and more. Currently, she works at Atlassian as a senior software engineer on the Statuspage team. In her spare time. she's the director of Women Who Code SF and helps early stage software teams scale their software platforms. Previously, she worked at Mavenlink, building highly usable and performant web products, like Full-Cycle Resource Management. Before Mavenlink, she led platform development for mRelief (YC W16), a tech non-profit, that has helped hundreds of thousands of low-income Americans get access to public assistance.",
  aboutShort:
    "Ellie is a technologist, product engineer, speaker, teacher, and more. She works at Atlassian as a senior software engineer on the Statuspage team. She's also the director of Women Who Code SF.",
  image: {
    url: "people/ellie.jpg",
  },
  social: {
    homepage: "https://heyellieday.com/",
    twitter: "heyellieday",
  },
  location: {
    country: {
      name: "USA",
      code: "US",
    },
    city: "San Francisco",
  },
  keywords: [Keyword.GRAPHQL],
  type: [ContactType.SPEAKER, ContactType.TALK],
};

export default person;
