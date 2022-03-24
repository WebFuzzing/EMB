import Keyword from "../../../server/schema/keywords";
import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/david-khourshid";

const talk: Session = {
  people: [speaker],
  title: "Mind-Reading with Adaptive and Intelligent UIs in React",
  description: `What if you could predict user behavior with smart UIs? In this talk, we will explore how we can make adaptive and intelligent UIs in React that learn from how individual users use your apps, and personalize the interface and features just for them, in real-time. With probability-driven statecharts, decision trees, reinforcement learning and more, UIs can be developed in such a way that it automatically adapts to the user's behavior.`,
  type: SessionType.TALK,
  keywords: [
    Keyword.STATE_MACHINES,
    Keyword.REINFORCEMENT_LEARNING,
    Keyword.ARTIFICIAL_INTELLIGENCE,
    Keyword.USER_INTERFACE,
  ],
  urls: {
    web: "https://slides.com/davidkhourshid/mind-reading-react-finland#/",
    video: "https://www.youtube.com/watch?v=ppvi09LIUnU",
  },
};

export default talk;
