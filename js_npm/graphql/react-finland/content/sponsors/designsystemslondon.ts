import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Design Systems London",
  about: "Open-source conference for the design systems community",
  image: {
    url: "sponsors/designsystemslondon.png",
  },
  social: {
    homepage: "https://www.designsystemslondon.com/",
    twitter: "DSLconf",
  },
  location: {
    country: {
      name: "Great Britain",
      code: "GB",
    },
    city: "London",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
