import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Facundo Giuliani",
  about:
    "Developer Relations Engineer at Storyblok. Systems Engineer. Full Stack Developer.",
  image: {
    url: "people/facundo.jpg",
  },
  social: {
    homepage: "https://fgiuliani.com/",
    twitter: "facundozurdo",
    github: "fgiuliani",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Argentina",
      code: "AR",
    },
    city: "Buenos Aires",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
