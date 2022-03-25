import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Ginetta",
  about: "We simplify",
  image: {
    url: "sponsors/ginetta.svg",
  },
  social: {
    github: "ginetta",
    homepage: "https://ginetta.net/",
    facebook: "ginettateam",
    instagram: "ginettateam",
    twitter: "ginettateam",
  },
  location: {
    country: {
      name: "Switzerland",
      code: "CH",
    },
    city: "Zürich",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
