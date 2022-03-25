import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "SAE",
  about: "Creative media institute",
  image: {
    url: "sponsors/sae.svg",
  },
  social: {
    homepage: "https://sae.edu",
    facebook: "",
    instagram: "",
    twitter: "",
  },
  location: {
    country: {
      name: "",
      code: "",
    },
    city: "",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
