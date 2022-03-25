import { Session, SessionType } from "../../../server/schema/Session";

import speaker from "../../people/carolyn-stransky";

const talk: Session = {
  people: [speaker],
  title: "Humanizing Your Documentation",
  description: ``,
  type: SessionType.TALK,
  keywords: [],
};

export default talk;
