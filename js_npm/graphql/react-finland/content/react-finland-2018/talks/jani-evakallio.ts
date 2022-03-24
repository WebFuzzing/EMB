import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/jani-evakallio";

const talk: Session = {
  people: [speaker],
  title: "The New Best Practices",
  description: `
When React was first introduced, it was ridiculed for going against established web development best practices as we knew them. Five years later, React is the gold standard for how we create user interfaces. 

Along the way, we’ve discovered a new set of tools, design patterns and programming techniques. In this talk, we’ll explore how we can apply the React philosophy to software engineering problems far beyond the React ecosystem.`,
  type: SessionType.KEYNOTE,
  keywords: speaker.keywords,
};

export default talk;
