import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Laura González",
  about:
    "Laura González is a designer slash developer that has done hijinks at places such as The Guardian and The Next Web. She loves all things JavaScript and CSS and in her free time she can be seen doing pointless npm modules or feeding squirrels at the park.",
  image: {
    url: "people/laura.jpg",
  },
  social: {
    homepage: "https://www.lauragonzalez.cc",
    twitter: "freezydorito",
    github: "walaura",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "walaura",
    youtube: "",
  },
  location: {
    country: {
      name: "Great Britain",
      code: "GB",
    },
    city: "London",
  },
  keywords: [Keyword.REACT], // TODO: check keywords for more
  type: [ContactType.SPEAKER],
};

export default person;
