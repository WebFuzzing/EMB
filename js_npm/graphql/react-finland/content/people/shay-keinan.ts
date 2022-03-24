import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Shay Keinan",
  about:
    "Shay is an experienced developer with a rich background in developing complex user interfaces, both for 2D and 3D. Currently Shay works at 500Tech on various projects, trains and consults companies and organizes the ReactJS meetup community.",
  image: {
    url: "people/shay.jpg",
  },
  social: {
    homepage: "",
    twitter: "Shay_Keinan",
    github: "crazypixel",
    linkedin: "shay-keinan",
  },
  location: {
    country: {
      name: "Israel",
      code: "IL",
    },
    city: "Tel Aviv",
  },
  keywords: [Keyword.REACT, Keyword.REACT_VR],
  type: [ContactType.SPEAKER],
};

export default person;
