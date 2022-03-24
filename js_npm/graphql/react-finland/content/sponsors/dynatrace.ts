import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Dynatrace",
  about: "Digitally transform faster, smarter, and easier",
  image: {
    url: "sponsors/dynatrace.svg",
  },
  social: {
    homepage: "https://www.dynatrace.com/",
    facebook: "",
    instagram: "",
    twitter: "",
  },
  location: {
    country: {
      name: "Austria",
      code: "AT",
    },
    city: "Vienna",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
