import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/rachel-nabors";

const talk: Session = {
  people: [speaker],
  title: "Secret keynote",
  description: "",
  type: SessionType.KEYNOTE,
  keywords: [],
};

export default talk;
