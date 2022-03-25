import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Nightingale Health",
  about:
    "Nightingale Health is aiming to solve the world’s biggest health issue",
  image: {
    url: "sponsors/nightingale.svg",
  },
  social: {
    homepage: "https://nightingalehealth.com/",
    facebook: "",
    twitter: "ngalehealth",
    linkedin: "company/3858219",
    youtube: "",
    instagram: "",
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
