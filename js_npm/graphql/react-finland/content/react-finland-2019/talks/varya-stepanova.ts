import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/varya-stepanova";

const talk: Session = {
  people: [speaker],
  title: "A practical guide to building your design system infrastructure",
  description: `Design systems bridge the gap between designers and developers, communicate shared practices to all the company levels and significantly decrease production costs. We all have heard this but how to achieve that in practice?

React ecosystem and community already provide a lot of efficient open source tools which can be used for building your design system infrastructure. With them, you can have the most automated development process, high-level interactive documentation for the libraries in your system and support contribution practices within your organisation.

This practical guide shows how to apply existing development tools to your system and what is the value you can get. 
`,
  type: SessionType.TALK,
  keywords: [],
  urls: {
    slides: "https://slides.react-finland.fi/2019/varya-stepanova.pdf",
    web: "https://varya.me/react-finland-2019/",
    video: "https://www.youtube.com/watch?v=5_lYTicLUbk",
  },
};

export default talk;
