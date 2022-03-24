import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Mike Berman",
  about:
    "Engineer @ Facebook, former organizer of Sydney Zombie Walk, & big fan of breakfast foods • 🎹 • ‍🤹 • ⛷ • 🦘🇦🇺‍",
  image: {
    url: "people/mike.jpg",
  },
  social: {
    homepage: "https://berman.xyz",
    twitter: "",
    github: "",
    linkedin: "mikeyberman",
  },
  location: {
    country: {
      name: "Great Britain",
      code: "GB",
    },
    city: "London",
  },
  keywords: [Keyword.REACT, Keyword.REDUX],
  type: [ContactType.SPEAKER],
};

export default person;
