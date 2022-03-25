import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Moon Highway",
  about: "Learn to code with Moon Highway",
  image: {
    url: "sponsors/moonhighway.svg",
  },
  social: {
    homepage: "https://www.moonhighway.com/",
    twitter: "moonhighway",
    github: "MoonHighway",
    linkedin: "moon-highway",
  },
  location: {
    country: {
      name: "United States",
      code: "US",
    },
    city: "",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
