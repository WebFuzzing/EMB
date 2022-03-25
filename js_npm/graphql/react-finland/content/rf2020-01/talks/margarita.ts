import { Session, SessionType } from "../../../server/schema/Session";

import speaker from "../../people/margarita";

const talk: Session = {
  people: [speaker],
  title: "Going beyond Redux - Diversify frontend state management",
  description: ``,
  type: SessionType.TALK,
  keywords: [],
};

export default talk;
