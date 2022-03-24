import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Accessibility Club",
  about: "Community events around web accessibility & assistive technology",
  image: {
    url: "sponsors/a11yclub.svg",
  },
  social: {
    homepage: "https://accessibility-club.org/",
    facebook: "",
    instagram: "",
    twitter: "",
  },
  location: {
    country: {
      name: "Germany",
      code: "DE",
    },
    city: "Nürnberg",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
