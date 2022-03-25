import Keyword from "../../../server/schema/keywords";
import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/nik-graf";

const talk: Session = {
  people: [speaker],
  title: "Un(popular) Opinions",
  description: `Unpopular opinions are mostly popular opinions people post on Twitter and share them as an unpopular opinion. Some unpopular opinions though are really unpopular for a good reason. Doesn’t make sense to you? Neither to me.`,
  type: SessionType.LIGHTNING_TALK,
  keywords: [Keyword.REACT],
  urls: {
    slides: "https://slides.react-finland.fi/2019/nik-graf.pdf",
    video: "https://www.youtube.com/watch?v=c9YtmMi-5rM",
  },
};

export default talk;
