import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Wunder",
  about: "Shaping the Digital Experience",
  image: {
    url: "sponsors/wunder.svg",
  },
  social: {
    homepage: "https://wunder.io/",
    facebook: "wunder.io",
    twitter: "Wunder_io",
    linkedin: "company/wunder.io",
    youtube: "UCsBoaJt5sX12IFBu1giXZJg",
    instagram: "wunder.io",
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
