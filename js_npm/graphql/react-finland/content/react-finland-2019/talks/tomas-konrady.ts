import Keyword from "../../../server/schema/keywords";
import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/tomas-konrady";

const talk: Session = {
  people: [speaker],
  title: "React Union: Write once, plug anywhere",
  description: `React is usually responsible for managing the render of the whole application. This talk will be about the different use case in which a React application is divided into several standalone configurable modules.

I am going to show how the React modules can be integrated into diverse CMS platforms such as WordPress or Liferay with help of React Union library.

Then we take a look at state management challenges in this kind of setup and how to solve them with @redux-tools.`,
  type: SessionType.TALK,
  keywords: [
    Keyword.CMS,
    Keyword.REACT,
    Keyword.REDUX,
    Keyword.STATE_MANAGEMENT,
    Keyword.LIVE_CODING,
  ],
  urls: {
    slides: "https://slides.react-finland.fi/2019/tomas-konrady.pdf",
    web: "https://finland-2019.firebaseapp.com/",
    video: "https://www.youtube.com/watch?v=gGzGzpaNlx4",
  },
};

export default talk;
