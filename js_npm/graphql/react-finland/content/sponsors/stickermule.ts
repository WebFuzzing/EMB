import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Stickermule",
  about: "Custom stickers that kick ass",
  image: {
    url: "sponsors/stickermule.svg",
  },
  social: {
    homepage:
      "https://www.stickermule.com/eu/uses/laptop-stickers?utm_source=sponsorship&utm_medium=referral&utm_campaign=ReactFinland19",
    github: "stickermule",
    facebook: "stickermule",
    instagram: "stickermule",
    twitter: "stickermule",
    youtube: "stickermule",
    dribble: "stickermule",
  },
  location: {
    country: {
      name: "Italy",
      code: "IT",
    },
    city: "Bologna",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
