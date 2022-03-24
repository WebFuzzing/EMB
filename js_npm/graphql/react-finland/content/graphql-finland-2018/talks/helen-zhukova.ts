import { Session, SessionType } from "../../../server/schema/Session";
import Keyword from "../../../server/schema/keywords";
import speaker from "../../people/helen-zhukova";

const talk: Session = {
  people: [speaker],
  title: "(Un)expected use of GraphQL",
  description: "",
  type: SessionType.LIGHTNING_TALK,
  keywords: [Keyword.GRAPHQL],
  urls: {
    web: "",
    slides: "http://slides.graphql-finland.fi/2018/07-helen-zhukova.pdf",
    video: "https://www.youtube.com/watch?v=9KG8mqo1iE0",
  },
};

export default talk;
