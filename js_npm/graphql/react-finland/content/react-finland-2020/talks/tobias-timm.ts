import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/tobias-timm";

const talk: Session = {
  people: [speaker],
  title: "",
  description: "",
  type: SessionType.LIGHTNING_TALK,
  keywords: [],
};

export default talk;
