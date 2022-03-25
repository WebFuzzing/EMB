import Keyword from "../../../server/schema/keywords";
import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/glenn-reyes";

const talk: Session = {
  people: [speaker],
  title: "Drawing the line between 3rd party and handcrafted components",
  description: `Often when we write new components, we might be like: Come on, there must be a library out there! And most times there was already someone who faced the same problem... only that it's not quite the same. Should I use the 3rd party lib? Or go with my own? Let's find out the differences and trade-offs in this session.`,
  type: SessionType.LIGHTNING_TALK,
  keywords: [Keyword.REACT, Keyword.LIBRARIES],
  urls: {
    slides: "https://slides.react-finland.fi/2019/glenn-reyes.pdf",
    web: "https://speakerdeck.com/glennreyes/drawing-the-line-between-3rd-party-and-handcrafted-code",
  },
};

export default talk;
