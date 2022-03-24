import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "ReasonConf",
  about:
    "World’s first Reason conference for web-developers & OCaml enthusiasts",
  image: {
    url: "sponsors/reason-conf.svg",
  },
  social: {
    homepage: "https://www.reason-conf.com/",
    facebook: "ReasonConf-1334078980027448",
    github: "reasonvienna",
    twitter: "reasonconf",
  },
  location: {
    country: {
      name: "Austria",
      code: "AT",
    },
    city: "Vienna",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
