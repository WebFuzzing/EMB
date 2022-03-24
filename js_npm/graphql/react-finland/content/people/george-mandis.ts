import { Contact, ContactType } from "../../server/schema/Contact";

const person: Contact = {
  name: "George Mandis",
  about:
    "Independent web developer and consultant, frequent traveler, occasional educator, perpetual optimist. Google Developer Expert in Web Technologies",
  image: {
    url: "people/george.jpg",
  },
  social: {
    homepage: "https://george.mand.is",
    twitter: "georgeMandis",
    github: "georgemandis",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
  },
  location: {
    country: {
      name: "United States",
      code: "US",
    },
    city: "Portland",
  },
  keywords: [],
  type: [ContactType.SPEAKER],
};

export default person;
