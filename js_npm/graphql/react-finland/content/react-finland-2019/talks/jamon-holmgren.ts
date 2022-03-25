import Keyword from "../../../server/schema/keywords";
import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/jamon-holmgren";

const talk: Session = {
  people: [speaker],
  title: "Building a Community Around Ignite",
  description: `In this talk, Jamon will go into what it took to build a thriving developer community around the popular React Native CLI and boilerplate, Ignite.`,
  type: SessionType.TALK,
  keywords: [Keyword.REACT_NATIVE],
  urls: {
    slides: "https://slides.react-finland.fi/2019/jamon-holmgren.pdf",
    web: "https://infinite-red.slides.com/infinitered/react-finland-2019?token=m2ZLCaid",
    video: "https://www.youtube.com/watch?v=gTG8_9Zv0YI",
  },
};

export default talk;
