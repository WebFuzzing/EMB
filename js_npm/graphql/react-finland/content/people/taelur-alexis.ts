import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Tae'lur Alexis",
  about:
    "Tae'lur Alexis is a Software Engineer with a love for building accessible UI and Founder of @CodeEverydayio. She is passionate about sharing her love for learning and helping people get into tech.",
  image: {
    url: "people/taelur.jpg",
  },
  social: {
    homepage: "",
    twitter: "TaelurAlexis",
    github: "cyberbarbie",
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
    city: "Seattle",
  },
  keywords: [Keyword.REACT], // TODO: check keywords for more
  type: [ContactType.SPEAKER],
};

export default person;
