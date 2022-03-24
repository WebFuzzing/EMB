import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Elisa Heikura",
  about:
    "Elisa is the Developer of People which is a fancy way of saying that she’s into soft skills such as communication, teamwork and self-awareness. For the past three years she has spent her days teaching and facilitating teams full of technical experts. Which, for her, is truly enjoyable work.",
  image: {
    url: "people/elisa.jpg",
  },
  social: {
    homepage: "https://koodarikuiskaaja.fi/",
    twitter: "elisaliisa",
    github: "",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "elisa-heikura",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
