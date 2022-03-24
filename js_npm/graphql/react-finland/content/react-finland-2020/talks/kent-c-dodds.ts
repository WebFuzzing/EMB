import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/kent-c-dodds";

const talk: Session = {
  people: [speaker],
  title: "",
  description: "",
  type: SessionType.KEYNOTE,
  keywords: [],
};

export default talk;
