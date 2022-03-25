import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Kenigbolo Meya Stephen",
  about:
    "Stephen is a Frontend Engineering Lead at BCaster and doubles as the Arch Conveyer/Community Manager of CodeAfrique, a not-for-profit weekend intensive software engineering Bootcamp for underrepresented groups in IT which was first started off in Estonia. During my free time, I sometimes volunteer as an open-source developer for https://adsbexchange.com, play golf and coach football (soccer as Americans call it).",
  image: {
    url: "people/kenigbolo.jpg",
  },
  social: {
    homepage: "https://www.kenigbolo.dev/",
    twitter: "expensivestevie",
    github: "kenigbolo",
    facebook: "kenigbolo.meyastephen",
    medium: "@kenigbolo",
    instagram: "",
    linkedin: "kenigbolo",
    youtube: "",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "Helsinki",
  },
  keywords: [Keyword.REACT], // TODO: check keywords for more
  type: [ContactType.SPEAKER],
};

export default person;
