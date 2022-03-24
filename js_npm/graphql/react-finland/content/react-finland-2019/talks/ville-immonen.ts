import Keyword from "../../../server/schema/keywords";
import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/ville-immonen";

const talk: Session = {
  people: [speaker],
  title: "React Standard Library",
  description: `React Native is the tool for building cross platform React apps. To build native-like experiences, we need good APIs providing the native capabilities of each platform in a unified way. Is the answer a standard library for React apps?`,
  type: SessionType.TALK,
  keywords: [Keyword.REACT_NATIVE, Keyword.UNIVERSAL_REACT],
  urls: {
    slides: "https://slides.react-finland.fi/2019/ville-immonen.pdf",
    video: "https://www.youtube.com/watch?v=6A9GeBXhPWY",
  },
};

export default talk;
