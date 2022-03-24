import { Contact, ContactType } from "../../server/schema/Contact";

const person: Contact = {
  name: "Samuli Hakoniemi",
  about: "",
  image: {
    url: "people/samuli.jpg",
  },
  social: {
    homepage: "",
    twitter: "zvona",
    github: "zvona",
    linkedin: "",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
  },
  keywords: [],
  type: [ContactType.ORGANIZER],
};

export default person;
