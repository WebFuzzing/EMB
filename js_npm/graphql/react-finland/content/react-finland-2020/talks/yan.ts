import Keyword from "../../../server/schema/keywords";
import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/yan";

const talk: Session = {
  people: [speaker],
  title: "Deep dive into Effector",
  description:
    "Effector is a new solution for state management. In this lightning talk, you'll learn what it is, how it works, and how you might find it useful in your daily work.",
  type: SessionType.LIGHTNING_TALK,
  keywords: [Keyword.STATE_MANAGEMENT],
};

export default talk;
