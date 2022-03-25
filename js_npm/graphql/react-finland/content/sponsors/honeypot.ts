import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Honeypot",
  about: "Choose a job you love",
  image: {
    url: "sponsors/honeypot.svg",
  },
  social: {
    homepage: "https://www.honeypot.io",
    facebook: "Honeypotio",
    instagram: "honeypot.io",
    twitter: "honeypotio",
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
