import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/xiaoru";

const talk: Session = {
  people: [speaker],
  title: "React for Command-Line Interfaces! (feat. Ink)",
  description: `Text-based CLI tools are great. They are fast, lightweight, and universally accessible. But what does CLI have to do with React?

In this lightning talk, we will have a look at Ink - a wonderful custom React renderer that allows us to use Flexbox to create excellent command-line interfaces with ease. Want to create a companion CLI tool for the next big thing that you are working on? Build it with React!`,
  type: SessionType.LIGHTNING_TALK,
  keywords: [],
};

export default talk;
