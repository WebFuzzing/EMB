import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Mika Schafroth",
  about:
    "Business-oriented software product engineer (M.Sc. in computer science) featuring 15 years of professional involvement in the entire life-cycle of digital products. Experience developing a variety of disruptive Internet technologies. Insights into modern travel, smart cities, the gig economy and social networking services.",
  image: {
    url: "people/mika.jpg",
  },
  social: {
    homepage: "",
    twitter: "",
    github: "mikaschafroth",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "mikaschafroth",
    youtube: "",
    devto: "",
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
