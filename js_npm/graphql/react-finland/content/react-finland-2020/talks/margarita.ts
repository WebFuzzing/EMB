import { Session, SessionType } from "../../../server/schema/Session";

import speaker from "../../people/margarita";

const talk: Session = {
  people: [speaker],
  title: "A tale of frontend state management",
  description: `If you ask two React developers which state management solution they use, likely they'll give you a different answer. In this session, we'll examine the evolution of state management patterns, where we are right now, and where we are heading.`,
  type: SessionType.LIGHTNING_TALK,
  keywords: [],
};

export default talk;
