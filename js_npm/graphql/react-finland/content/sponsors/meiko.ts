import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Meiko",
  about: "Digital tailor for visionaries.",
  image: {
    url: "sponsors/meiko.svg",
  },
  social: {
    homepage: "https://meiko.fi?from=reactfinland2019",
    facebook: "https://www.facebook.com/meiko.fi/",
    instagram: "https://www.instagram.com/meikofi/?hl=fi",
    linkedin: "",
    twitter: "",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "Jyväskylä",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
