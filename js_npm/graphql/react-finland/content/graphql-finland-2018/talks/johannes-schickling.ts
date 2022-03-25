import { Session, SessionType } from "../../../server/schema/Session";
import Keyword from "../../../server/schema/keywords";
import speaker from "../../people/johannes-schickling";

const talk: Session = {
  people: [speaker],
  title: "End-to-end type-safety with GraphQL",
  description: `GraphQL is becoming the new standard for API development and shows rapid adoption both on the client and server. This talk dives deep into one of the most powerful features of GraphQL - its type-system. See how GraphQL can be used to enable end-to-end type-safety across any language, making your application architecture more resilient and easier to evolve.`,
  type: SessionType.LIGHTNING_TALK,
  keywords: [Keyword.GRAPHQL],
  urls: {
    web: "",
    slides: "http://slides.graphql-finland.fi/2018/08-johannes-schickling.pdf",
    video: "https://www.youtube.com/watch?v=5JuSXwFSJn4",
  },
};

export default talk;
