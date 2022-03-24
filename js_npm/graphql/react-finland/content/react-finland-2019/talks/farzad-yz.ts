import Keyword from "../../../server/schema/keywords";
import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/farzad-yz";

const talk: Session = {
  people: [speaker],
  title: "Abstract component modeling in React",
  description: `Often when we need to develop user interfaces, we model the interface behaviour based on a visual design or the platform we ship onto. However, since the behaviour of the component is the same, it would be beneficial to find a practical way to share this behaviour between different platforms, styling systems, state containers, data transmitters and any other sort of integrations.
    
In this talk, I'll be showing a set of techniques to model a semi complicated React component, independent from the common integrations.`,
  type: SessionType.LIGHTNING_TALK,
  keywords: [
    Keyword.STATE_MANAGEMENT,
    Keyword.STATECHARTS,
    Keyword.HEADLESS_COMPONENTS,
  ],
  urls: {
    slides: "https://slides.react-finland.fi/2019/farzad-yz.pdf",
    web: "https://abstract-component-modeling.netlify.com",
    video: "https://www.youtube.com/watch?v=AVb5dURO6q0",
  },
};

export default talk;
