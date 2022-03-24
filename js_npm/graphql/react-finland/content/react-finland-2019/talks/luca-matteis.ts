import Keyword from "../../../server/schema/keywords";
import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/luca-matteis";

const talk: Session = {
  people: [speaker],
  title: "Append-only development with React",
  description: `Behavioral Programming is a new paradigm that changes how we think about developing software. Programs are constructed in an incremental-fashion where newly added code can change how old code is executed. In this talk I will explain this paradigm in the context of developing a React app. Furthermore I will show how changes can be made to the app without having to understand how old code was written; hence I describe this method as "append-only development". `,
  type: SessionType.TALK,
  keywords: [
    Keyword.BEHAVIORAL_PROGRAMMING,
    Keyword.REACT,
    Keyword.GENERATORS,
    Keyword.EVENTS,
  ],
  urls: {
    slides: "https://slides.react-finland.fi/2019/luca-matteis.pdf",
    web: "https://lmatteis.github.io/finland-talk/assets/player/KeynoteDHTMLPlayer.html",
    video: "https://www.youtube.com/watch?v=cXuvCMG21Ss",
  },
};

export default talk;
