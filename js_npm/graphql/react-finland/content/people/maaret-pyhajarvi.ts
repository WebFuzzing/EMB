import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Maaret Pyhäjärvi",
  about:
    "Feedback fairy with a day-job at F-Secure as Eng Manager. Tester, (Polyglot) Programmer, Speaker, Author, Conference Organizer. She/Her.",
  image: {
    url: "people/maaret.jpg",
  },
  social: {
    homepage: "http://maaretp.com/",
    twitter: "maaretp",
    github: "maaretp",
    linkedin: "",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "Helsinki",
  },
  keywords: [Keyword.REACT, Keyword.TESTING],
  type: [ContactType.SPEAKER],
};

export default person;
