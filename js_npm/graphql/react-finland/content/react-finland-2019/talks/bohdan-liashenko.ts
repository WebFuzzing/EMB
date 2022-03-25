import Keyword from "../../../server/schema/keywords";
import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/bohdan-liashenko";

const talk: Session = {
  people: [speaker],
  title: "Codecrumbs in React",
  description: `The granular approach of designing components helps us to build reusable and encapsulated features. On another hand, it also makes a codebase much more complex by extra added abstractions and drastically increased amount of source code files. How can we navigate the maze of hundreds of React-atoms now? Join my talk to get the answer.`,
  type: SessionType.LIGHTNING_TALK,
  keywords: [Keyword.TOOLING],
  urls: {
    slides: "https://slides.react-finland.fi/2019/bohdan-liashenko.pdf",
    web: "https://docs.google.com/presentation/d/1Uj_5wyafZnp-BaSHzEne02gSxKJgmmjQK9M6mG1fi3k/edit#slide=id.p",
    video: "https://www.youtube.com/watch?v=S_1-1jzLxm4",
  },
};

export default talk;
