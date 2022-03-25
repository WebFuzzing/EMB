import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Madalyn Rose Parker",
  about:
    "Working to make the web a better place. Mental health advocate, passionate about a11y, requires strong coffee.",
  image: {
    url: "people/madalyn.jpg",
  },
  social: {
    homepage: "",
    twitter: "madalynrose",
    github: "madalynrose",
    facebook: "",
    medium: "madalynrose",
    instagram: "madalynrose09",
    linkedin: "madalynparker",
    youtube: "",
  },
  location: {
    country: {
      name: "United States",
      code: "US",
    },
    city: "Ann Arbor, Michigan",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
