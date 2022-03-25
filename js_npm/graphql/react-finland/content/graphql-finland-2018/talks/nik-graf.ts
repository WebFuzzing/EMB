import { Session, SessionType } from "../../../server/schema/Session";
import Keyword from "../../../server/schema/keywords";
import speaker from "../../people/nik-graf";

const talk: Session = {
  people: [speaker],
  title: "Reason and GraphQL",
  description: `Using Reason's type inference we can create GraphQL servers with 100% type coverage. Regardless if we compile to Node.js or native binaries using Reason we can do this with ease.

Besides that Reason shines even more so on the client. Send one quick introspection request and you get full autocompletion on your schema right in the browser.`,
  type: SessionType.LIGHTNING_TALK,
  keywords: [Keyword.GRAPHQL],
  urls: {
    web: "",
    slides: "http://slides.graphql-finland.fi/2018/11-nik-graf.pdf",
    video: "https://www.youtube.com/watch?v=tUIvl2pe8CQ",
  },
};

export default talk;
