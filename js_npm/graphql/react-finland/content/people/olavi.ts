import { Contact, ContactType } from "../../server/schema/Contact";

const person: Contact = {
  name: "Olavi Haapala",
  about:
    "Software Developer at Futurice helping clients build fast and accessible experiences on the web. Passionate about web performance and accessibility.",
  image: {
    url: "people/olavi.jpg",
  },
  social: {
    homepage: "https://olavihaapala.fi",
    twitter: "0lpeh",
    github: "olpeh",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "olavi-haapala-b7b752162",
    youtube: "",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "Helsinki",
  },
  keywords: [],
  type: [ContactType.SPEAKER],
};

export default person;
