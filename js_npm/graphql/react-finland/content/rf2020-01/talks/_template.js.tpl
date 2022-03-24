
import { Session, SessionType } from "../../../server/schema/Session";

// TODO: Update to point to the right speaker
import speaker from "../../people/<speaker>";

const talk: Session = {
  people: [speaker],
  title: "",
  description: "",
  type: SessionType.TALK,
  keywords: [],
};

export default talk;
