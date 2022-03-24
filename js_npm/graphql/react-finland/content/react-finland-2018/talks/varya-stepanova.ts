import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/varya-stepanova";

const talk: Session = {
  people: [speaker],
  title: "How to use React, webpack and other buzzwords if there is no need",
  description:
    "The best way to study a new development approach is to do it in practice. But good projects built with modern technologies most often require developers with experience in these technologies. One of the options to get this is a side project. What can it be in React?\n\nVarya will show how to generate a multilingual static website using Metalsmith, React and other modern technologies and tools. For over a year, she has been using this stack to build her personal blog and it turned out to be expressive, fast and convenient. After all, it’s our beloved React.",
  type: SessionType.LIGHTNING_TALK,
  keywords: speaker.keywords,
  urls: {
    web: "http://varya.me/react-finland-2018/",
  },
};

export default talk;
