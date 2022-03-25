import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Farzad YousefZadeh",
  about:
    "Lead engineer @epicgames. Into State machines and Statecharts. Coach @mentorcruise",
  image: {
    url: "people/farzad.jpg",
  },
  social: {
    homepage: "https://farzadyz.com/",
    twitter: "farzad_yz",
    github: "farskid",
    facebook: "",
    medium: "farzad_yz",
    instagram: "",
    linkedin: "farzad_yz",
    youtube: "",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "Helsinki",
  },
  keywords: [
    Keyword.STATE_MANAGEMENT,
    Keyword.STATECHARTS,
    Keyword.HEADLESS_COMPONENTS,
    Keyword.ACTOR_MODEL,
  ],
  type: [ContactType.SPEAKER],
};

export default person;
