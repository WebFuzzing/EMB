import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Saku Tihveräinen",
  about: `Lead Recruitment Consultant / Part-time PhD Researcher

Saku is a Lead Recruitment Consultant with over ten years of experience from the field. Alongside the work, Saku works gradually his way through the PhD ("how professionals choose jobs"). When not working, you can find him by the junior football pitch or jogging on the shores of Helsinki.`,
  image: {
    url: "people/saku.jpg",
  },
  social: {
    homepage: "",
    twitter: "SakuTihverainen",
    github: "",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "sakutihverainen",
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
