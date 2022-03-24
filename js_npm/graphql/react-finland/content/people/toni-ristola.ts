import { Contact, ContactType } from "../../server/schema/Contact";

const person: Contact = {
  name: "Toni Ristola",
  about:
    "Toni Ristola is a Software Architect at Robu Oy and an organizer for the React Finland conference and contributor for the React Finland App.",
  image: {
    url: "people/toni.jpg",
  },
  social: {
    homepage: "",
    twitter: "toniristola",
    github: "tristola",
    linkedin: "toniristola",
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
