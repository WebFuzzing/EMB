import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Smartly",
  about: "Powering Beautifully Effective Ads",
  image: {
    url: "sponsors/smartly.svg",
  },
  social: {
    homepage: "https://www.smartly.io",
    facebook: "",
    instagram: "",
    twitter: "smartlyio",
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
