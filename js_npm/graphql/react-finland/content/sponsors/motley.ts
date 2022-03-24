import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Motley",
  about: "We create better futures for businesses",
  image: {
    url: "sponsors/motley.svg",
  },
  social: {
    homepage: "https://motley.fi/",
    facebook: "Motleyagency",
    github: "motleyagency",
    linkedin: "company/2375211",
    instagram: "motleyagency",
    twitter: "motleyagency",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "Helsinki",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
