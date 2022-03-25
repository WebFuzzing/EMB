import { Contact, ContactType } from "../../server/schema/Contact";

const person: Contact = {
  name: "Bodil Stokke",
  about: "",
  image: {
    url: "people/bodil.jpg",
  },
  social: {
    homepage: "http://bodil.lol",
    twitter: "bodil",
    github: "bodil",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
  },
  location: {
    country: {
      name: "Great Britain",
      code: "GB",
    },
    city: "London",
  },
  keywords: [],
  type: [ContactType.SPEAKER],
};

export default person;
