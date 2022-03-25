import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Codento",
  about: "A Change Agent's Right Hand.",
  image: {
    url: "sponsors/codento.png",
  },
  social: {
    homepage: "https://www.codento.fi/en/",
    twitter: "codento",
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
