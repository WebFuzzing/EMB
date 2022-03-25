import { Contact, ContactType } from "../../server/schema/Contact";

const person: Contact = {
  name: "Aleksi Pousar",
  about:
    "Aleksi Pousar works on the app and is the treasurer of the association.",
  image: {
    url: "people/aleksi.jpg",
  },
  social: {
    homepage: "https://aleksipousar.fi",
    twitter: "AleksiPousar",
    github: "stoneman1",
    linkedin: "aleksipousar",
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
