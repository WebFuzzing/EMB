import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "David Khourshid",
  about:
    "David Khourshid is a Florida-based web developer for Microsoft, a tech author, and speaker. Also a fervent open-source contributor, he is passionate about JavaScript, CSS, animation, innovative user interfaces, and cutting-edge front-end technologies. When not behind a computer keyboard, he’s behind a piano keyboard or traveling.",
  image: {
    url: "people/david.jpg",
  },
  social: {
    homepage: "",
    twitter: "davidkpiano",
    github: "davidkpiano",
    linkedin: "david-khourshid-760055125",
  },
  location: {
    country: {
      name: "United States",
      code: "US",
    },
    city: "Orlando",
  },
  keywords: [Keyword.REACT, Keyword.STATE_MANAGEMENT],
  type: [ContactType.SPEAKER],
};

export default person;
