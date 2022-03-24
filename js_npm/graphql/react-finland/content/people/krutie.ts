import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Krutie Patel",
  about: `I'm a front-end developer. I create interactive web stuff using Vue js and Nuxt js. Some other frameworks and libraries I also enjoy working with include GSAP, Laravel ⚡️ and Jamstack (JavaScript, APIs + Markup). I love writing ✏️ articles about what I learn and share with Vue/Nuxt community. You can find my blog-posts on my [ website](https://krutiepatel.com/blog), which is also created using 👑 Nuxt js.

Apart from this, I also love creating [colour-coded diagrams](https://krutiepatel.com/diagrams). I create them to capture and document my learning. Diagraming have been an effective tool in my learning journey of Laravel, Vue js and of course, my absolute favourite, Nuxt js.`,
  image: {
    url: "people/krutie.jpg",
  },
  social: {
    homepage: "https://krutiepatel.com/",
    twitter: "KrutiePatel",
    github: "Krutie",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Australia",
      code: "AU",
    },
    city: "",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
