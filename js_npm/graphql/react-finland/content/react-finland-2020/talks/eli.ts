import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/eli";

const talk: Session = {
  people: [speaker],
  title: "What is the deal with i18n?",
  description: "While about 51% of the world’s websites are in English, only about 25% of web users are English speakers. With half of the world’s population online, internationalisation is as relevant as ever. Let’s explore what i18n means, how it gets made on the web and what we should keep in mind or avoid when making the web more accessible to the world.",
  type: SessionType.TALK,
  keywords: [],
};

export default talk;
