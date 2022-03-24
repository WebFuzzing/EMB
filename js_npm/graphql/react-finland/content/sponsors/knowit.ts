import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Knowit",
  about: `Makers of a sustainable future`,
  image: {
    url: "sponsors/knowit.svg",
  },
  social: {
    homepage: "https://www.knowit.fi/",
    facebook: "KnowitOy",
    linkedin: "company/knowit",
    twitter: "knowitsuomi",
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
