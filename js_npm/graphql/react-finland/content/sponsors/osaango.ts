import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Osaango",
  about:
    "Learn the next big thing - Skills & Methods for API, Platform and AI Economy",
  image: {
    url: "sponsors/osaango.png",
  },
  social: {
    homepage: "https://www.osaango.com/",
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
