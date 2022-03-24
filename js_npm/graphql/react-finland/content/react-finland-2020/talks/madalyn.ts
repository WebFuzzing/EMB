import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/madalyn";

const talk: Session = {
  people: [speaker],
  title: "",
  description: "", // Session description goes here. ES6 template and markdown work
  type: SessionType.TALK,
  keywords: [],
};

export default talk;
