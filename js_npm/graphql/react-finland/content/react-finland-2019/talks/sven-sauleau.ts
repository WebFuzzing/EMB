import Keyword from "../../../server/schema/keywords";
import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/sven-sauleau";

const talk: Session = {
  people: [speaker],
  title: "WebAssembly - The Next Big Platform",
  description: `JavaScript has had the monopoly on the web for quite some time now. What if, for performance reasons or preference, you want to use another language on the web or mix them?

WebAssembly is the right tool for the job! I want to demonstrate how you can write parts of your React application using Rust and related tools. To complete the demonstration, the code will be deployed on Cloudflare's serverless platform.

Rust ecosystem offers many great tools to work with WebAssembly on the web. These include DOM manipulation, JavaScript bindings and so on, and you will learn more about them in the talk.`,
  type: SessionType.LIGHTNING_TALK,
  keywords: [
    Keyword.LIVE_CODING,
    Keyword.REACT,
    Keyword.RUST,
    Keyword.SERVERLESS,
    Keyword.WEB_ASSEMBLY,
  ],
  urls: {
    slides: "https://slides.react-finland.fi/2019/sven-sauleau.pdf",
    video: "https://www.youtube.com/watch?v=8xhLsVVqchg",
  },
};

export default talk;
