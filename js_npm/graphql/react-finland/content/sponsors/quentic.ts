import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Quentic",
  about: "Connect. Engage. Inspire.",
  image: {
    url: "sponsors/quentic.svg",
  },
  social: {
    homepage: "https://www.quentic.fi",
    facebook: "",
    instagram: "",
    twitter: "",
  },
  location: {
    country: {
      name: "Germany",
      code: "DE",
    },
    city: "Berlin",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
