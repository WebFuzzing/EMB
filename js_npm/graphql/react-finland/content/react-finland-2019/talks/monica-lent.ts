import Keyword from "../../../server/schema/keywords";
import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/monica-lent";

const talk: Session = {
  people: [speaker],
  title: "Building resilient frontend architecture",
  description: `Change is inevitable. So is legacy. And too often, we as developers (who love to solve problems by coding) fall into the trap of believing the only way to fix it is by rewriting everything again and again. But how can we design an application architecture that is more resilient to change in the first place? In this talk we’ll look at specific techniques you can use today to keep your app from turning into that infamous ball of mud.`,
  type: SessionType.TALK,
  keywords: [Keyword.CODE_QUALITY, Keyword.TOOLING, Keyword.ARCHITECTURE],
  urls: {
    slides: "https://slides.react-finland.fi/2019/monica-lent.pdf",
    video: "https://www.youtube.com/watch?v=brMZLmZ1HR0",
  },
};

export default talk;
