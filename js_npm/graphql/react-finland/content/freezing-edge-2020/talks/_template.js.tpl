
import { Session, SessionType } from "../../../server/schema/Session";

// TODO: Update to point to the right speaker
import speaker from "../../people/<speaker>";

const talk: Session = {
  people: [speaker],
  title: "", // The title of the session goes here
  description: "", // Session description goes here. ES6 template and markdown work
  type: SessionType.TALK, // SessionType.LIGHTNING_TALK, SessionType.KEYNOTE, SessionType.WORKSHOP are possible too
  keywords: [],
};

export default talk;
