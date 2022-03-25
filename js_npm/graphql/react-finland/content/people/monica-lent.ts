import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Monica Lent",
  about:
    "Monica is the Frontend Lead at SumUp, a multinational FinTech startup based in Berlin. She began coding at the tender age of ten years old, and somehow managed to pivot from studying Latin in university to where she is today, leading a team of developers distributed in three countries. Before moving abroad, Monica worked in domains such as natural language processing and cloud technologies.",
  image: {
    url: "people/monica.jpg",
  },
  social: {
    homepage: "https://monicalent.com/",
    twitter: "monicalent",
    github: "mlent",
    linkedin: "monica-lent-542122145",
  },
  location: {
    country: {
      name: "Germany",
      code: "DE",
    },
    city: "Berlin",
  },
  keywords: [Keyword.REACT, Keyword.ARCHITECTURE, Keyword.TOOLING],
  type: [ContactType.SPEAKER],
};

export default person;
