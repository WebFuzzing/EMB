import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Reaktor",
  about: "We realize visions.",
  image: {
    url: "sponsors/reaktor.svg",
  },
  social: {
    homepage: "https://www.reaktor.com/",
    facebook: "",
    github: "reaktor",
    linkedin: "company/reaktor_",
    instagram: "",
    twitter: "ReaktorNow",
    youtube: "",
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
