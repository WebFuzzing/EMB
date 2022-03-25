import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Eli Schutze Ramirez",
  about:
    "Originally from Nicaragua, Eli is a London-based web engineer, speaker and community organiser. She is currently helping make money work for everyone at Monzo bank. When she's not working you can probably find her at meetups, conferences or on Twitter.",
  image: {
    url: "people/eli.jpg",
  },
  social: {
    homepage: "",
    twitter: "elibelly",
    github: "elischutze",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Great Britain",
      code: "GB",
    },
    city: "London",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
