import Keyword from "../../../server/schema/keywords";
import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/christoffer-niska";

const talk: Session = {
  people: [speaker],
  title: "Releasing a library written in TypeScript on NPM",
  description: `Most of us have used or at least heard about TypeScript, but how many have released a React library written in TypeScript on NPM?

  In this session we will take a look at the tooling required and some best practices for publishing a library using TypeScript and Rollup.`,
  type: SessionType.LIGHTNING_TALK,
  keywords: [Keyword.NPM, Keyword.TYPESCRIPT],
  urls: {
    slides: "https://slides.react-finland.fi/2019/christoffer-niska.pdf",
    video: "https://www.youtube.com/watch?v=T_4CCdkN468",
  },
};

export default talk;
