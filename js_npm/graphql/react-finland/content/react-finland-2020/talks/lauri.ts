import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/lauri";

const talk: Session = {
  people: [speaker],
  title: "From React Router 3 to 4",
  description:
    "What's the difference between RR 3 and 4. Why did we spend one month migrating 270kloc to it?",
  type: SessionType.LIGHTNING_TALK,
  keywords: [],
};

export default talk;
