import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "JSCamp",
  about:
    "Two-day International Javascript Conference crafted for and by the community. Learn about the latest trends, meet core team members of different frameworks, tooling and more!",
  image: {
    url: "sponsors/jscamp.svg",
  },
  social: {
    homepage: "https://jscamp.tech/",
    facebook: "jscamp",
    youtube: "jscamp",
    twitter: "jscamp",
  },
  location: {
    country: {
      name: "Spain",
      code: "ES",
    },
    city: "Barcelona",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
