import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Gofore",
  about: "We create tomorrow's digital services and work culture today",
  image: {
    url: "sponsors/gofore.svg",
  },
  social: {
    homepage: "https://gofore.com/en/home/",
    facebook: "GoforeGroup",
    github: "gofore",
    linkedin: "company/gofore",
    instagram: "goforegroup",
    twitter: "GoforeGroup",
    youtube: "UCp7QetJnfgp3QvP7oWaclvQ",
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
