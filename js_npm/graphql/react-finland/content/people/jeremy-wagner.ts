import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Jeremy Wagner",
  about:
    "I'm a web performance consultant, developer, writer, and speaker. I work to make the web faster for everyone, everywhere.",
  image: {
    url: "people/jeremy.jpg",
  },
  social: {
    homepage: "https://jeremy.codes",
    twitter: "malchata",
    github: "malchata",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
  },
  location: {
    country: {
      name: "United States",
      code: "US",
    },
    city: "Lakeville",
  },
  keywords: [Keyword.REACT], // TODO: check keywords for more
  type: [ContactType.SPEAKER],
};

export default person;
