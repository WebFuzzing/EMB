import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/kenigbolo-meya-stephen";

const talk: Session = {
  people: [speaker],
  title: "Documentation in React Code - A love/hate paradigm",
  description: "",
  type: SessionType.TALK,
  keywords: [],
};

export default talk;
