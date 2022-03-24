import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Nik Graf",
  about:
    "Nik cares about good UX and development tools. He co-created several popular open source projects like DraftJS Plugins and Polished. Nik participated in Stripe’s Open Source Retreat and joined Serverless Inc. to deploy auto-scaling infrastructures. In his spare-time he enjoys writing code in Reason and play with WebVR as well as organising the ReactJS Vienna meetup.",
  aboutShort:
    "Nik cares about good UX and development tools. He co-created several popular open source projects like DraftJS Plugins and Polished. Nik isa also organizer of Reason Conf and React Vienna meetup.",
  image: {
    url: "people/nik.jpg",
  },
  social: {
    homepage: "http://www.nikgraf.com/",
    twitter: "nikgraf",
    github: "nikgraf",
    linkedin: "nikolausgraf",
  },
  location: {
    country: {
      name: "Austria",
      code: "AT",
    },
    city: "Vienna",
  },
  keywords: [Keyword.REASON, Keyword.GRAPHQL, Keyword.REACT, Keyword.REACT_VR],
  type: [ContactType.SPEAKER],
};

export default person;
