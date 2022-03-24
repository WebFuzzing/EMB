import { Session, SessionType } from "../../../server/schema/Session";
import Keyword from "../../../server/schema/keywords";
import speaker from "../../people/eloy-duran";

const talk: Session = {
  people: [speaker],
  title: "Where art thou, my error?",
  description: "",
  type: SessionType.TALK,
  keywords: [Keyword.GRAPHQL],
  urls: {
    web: "",
    slides: "http://slides.graphql-finland.fi/2018/14-eloy-duran.pdf",
    video: "https://www.youtube.com/watch?v=qKA-n8p-sNc",
  },
};

export default talk;
