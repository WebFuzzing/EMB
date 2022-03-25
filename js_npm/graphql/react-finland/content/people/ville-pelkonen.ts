import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Ville Pelkonen",
  about:
    "Ville is an inspired front end prodigy who started his deep dive into React Native over 3 years ago, and still hasn’t resurfaced. He’s a forward-leaning tech driver often found rambling about UX, accessibility, processes or psychology. Ville tends to get involved wherever anything regarding those could be improved. Currently, Ville works as a Senior Software Engineer and Web Tech Lead at Qvik. Spend a few minutes with him, you’ll learn something new!",
  image: {
    url: "people/ville-pelkonen.jpg",
  },
  social: {
    homepage: "",
    twitter: "",
    github: "",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "Helsinki",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
