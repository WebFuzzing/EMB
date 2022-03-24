import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Andrey Okonetchnikov",
  about:
    "Andrey is a front-end engineer and UI designer from Russia who lives in Austria and works on modern user interfaces using web technologies. As an active Open Source contributor, he contributed to Webpack, React.js, and Jest; designed the PostCSS official website and created a bunch of logos for other OSS projects.\n\nAndrey is a co-organizer of the ReactVienna community, designer, and co-creator of ColorSnapper. He loves mountain biking, snowboarding, and specialty coffee.",
  image: {
    url: "people/andrey.jpg",
  },
  social: {
    homepage: "http://okonet.ru/",
    twitter: "okonetchnikov",
    github: "okonet",
  },
  location: {
    country: {
      name: "Austria",
      code: "AT",
    },
    city: "Vienna",
  },
  keywords: [
    Keyword.REACT,
    Keyword.STYLING,
    Keyword.STYLE_GUIDES,
    Keyword.TOOLING,
  ],
  type: [ContactType.SPEAKER],
};

export default person;
