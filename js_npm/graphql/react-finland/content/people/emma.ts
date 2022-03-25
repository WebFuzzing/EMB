import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Emma Bostian",
  about:
    "Emma Bostian is a UX Engineer based in Karlsruhe, Germany. She began her career as a Software Engineer at IBM in Austin, Texas, and three years later picked up and moved abroad. She currently focuses on building Design Systems with React. She founded and manages an open-source project, Coding Coach, is a co-host of the Ladybug Podcast.",
  image: {
    url: "people/emma.jpg",
  },
  social: {
    homepage: "",
    twitter: "emmabostian",
    github: "emmabostian",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
  },
  location: {
    country: {
      name: "Germany",
      code: "DE",
    },
    city: "Karlsruhe",
  },
  keywords: [Keyword.REACT], // TODO: check keywords for more
  type: [ContactType.SPEAKER],
};

export default person;
