import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Eeva-Jonna 'Eevis' Panula",
  about:
    "Eevis is a software developer and accessibility specialist based in Finland. She loves sharing information about topics like accessibility, React and GraphQL, and is active in different communities aiming to increase equality in the tech field in Finland. When not coding, she explores the world around her with a kayak. ",
  image: {
    url: "people/eevis.jpg",
  },
  social: {
    homepage: "https://eevis.codes",
    twitter: "EevisPanula",
    github: "eevajonnapanula",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "eevajonna",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "Helsinki",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
