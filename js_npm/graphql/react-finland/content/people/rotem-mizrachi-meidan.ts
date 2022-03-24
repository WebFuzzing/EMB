import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Rotem Mizrachi-Meidan",
  about:
    "Rotem is a Software Engineer, open source advocate, passionate about Android, React Native, mobile performance, writing developer tools and Lego! In his current position at Wix, Rotem is working with React Native, writing infrastructure and testing tools.",
  image: {
    url: "people/rotem.jpg",
  },
  social: {
    homepage: "",
    twitter: "rotemmiz",
    github: "rotemmiz",
    linkedin: "rotem-mizrachi-meidan-a2306a14",
  },
  location: {
    country: {
      name: "Israel",
      code: "IL",
    },
    city: "Tel Aviv",
  },
  keywords: [
    Keyword.DETOX,
    Keyword.REACT,
    Keyword.REACT_NATIVE,
    Keyword.TESTING,
    Keyword.TOOLING,
  ],
  type: [ContactType.SPEAKER],
};

export default person;
