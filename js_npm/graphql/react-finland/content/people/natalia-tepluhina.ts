import { Contact, ContactType } from "../../server/schema/Contact";

const person: Contact = {
  name: "Natalia Tepluhina",
  about:
    "Vue.js core team member, Senior Frontend Engineer @GitLab, GDE, and coffee lover.",
  image: {
    url: "people/natalia.jpg",
  },
  social: {
    homepage: "",
    twitter: "N_Tepluhina",
    github: "NataliaTepluhina",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
  },
  location: {
    country: {
      name: "Ukraine",
      code: "UA",
    },
    city: "Kiev",
  },
  keywords: [],
  type: [ContactType.SPEAKER],
};

export default person;
