import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Valamis",
  about: "We help industry-leading enterprises execute their vision",
  image: {
    url: "sponsors/valamis.svg",
  },
  social: {
    homepage: "https://www.valamis.com",
    facebook: "valamisgroup",
    github: "valamis",
    linkedin: "company/valamis",
    instagram: "valamis",
    twitter: "valamis",
    youtube: "UCs3v9ObvcknbJq0xALSqj-Q",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "Joensuu",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
