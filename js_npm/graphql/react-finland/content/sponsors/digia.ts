import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Digia",
  about: "Smoother digital world",
  image: {
    url: "sponsors/digia.svg",
  },
  social: {
    homepage: "https://digia.com/",
    twitter: "digiaonline",
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
