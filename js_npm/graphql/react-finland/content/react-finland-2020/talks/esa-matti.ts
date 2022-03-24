import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/esa-matti";

const talk: Session = {
  people: [speaker],
  title: "",
  description: "",
  type: SessionType.LIGHTNING_TALK,
  keywords: [],
};

export default talk;
