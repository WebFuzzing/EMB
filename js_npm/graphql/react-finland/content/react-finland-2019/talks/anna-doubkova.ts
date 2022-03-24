import Keyword from "../../../server/schema/keywords";
import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/anna-doubkova";

const talk: Session = {
  people: [speaker],
  title: "Practical Performance for React (Native)",
  description: `There are quite a few theoretical concepts of writing good a React application, including popular functional programming approaches. However, few take into consideration the impacts these will have on performance of the application. In this talk you’ll find out how some commonly used patterns have an adverse effect on the speed of your app, and how to avoid using them. We’ll mainly explore UI and state management but we’ll also dive into some native-specific issues and look at how to prevent them.`,
  type: SessionType.TALK,
  keywords: [Keyword.REACT, Keyword.REACT_NATIVE],
  urls: {
    slides: "https://slides.react-finland.fi/2019/anna-doubkova.pdf",
    video: "https://www.youtube.com/watch?v=W2a5_Qn2jVI",
  },
};

export default talk;
