import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "YGLF Kyiv",
  about: "Community event built by developers for developers",
  image: {
    url: "sponsors/yglf.jpg",
  },
  social: {
    homepage: "http://yglf.com.ua/",
    facebook: "yglf.kyiv",
    twitter: "yglf_kyiv",
    youtube: "UCU-fOxx_kT5OARG0KiksiCA",
  },
  location: {
    country: {
      name: "Ukraine",
      code: "UA",
    },
    city: "Kiev",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
