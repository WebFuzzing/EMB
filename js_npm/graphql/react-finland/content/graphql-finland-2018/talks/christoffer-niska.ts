import { Session, SessionType } from "../../../server/schema/Session";
import Keyword from "../../../server/schema/keywords";
import speaker from "../../people/christoffer-niska";

const talk: Session = {
  people: [speaker],
  title: "graphql-php",
  description: "",
  type: SessionType.LIGHTNING_TALK,
  keywords: [Keyword.GRAPHQL],
  urls: {
    web: "",
    slides: "http://slides.graphql-finland.fi/2018/12-christoffer-niska.pdf",
    video: "https://www.youtube.com/watch?v=WDGj0zbEjL8",
  },
};

export default talk;
