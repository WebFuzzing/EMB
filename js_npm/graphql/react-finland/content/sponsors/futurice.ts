import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Futurice",
  about: "You can’t predict the future. Are you prepared to define it?",
  image: {
    url: "sponsors/futurice.svg",
  },
  social: {
    homepage: "https://www.futurice.com/",
    facebook: "futurice",
    github: "futurice",
    linkedin: "company/futurice",
    youtube: "",
    instagram: "",
    twitter: "futurice",
    pinterest: "",
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
