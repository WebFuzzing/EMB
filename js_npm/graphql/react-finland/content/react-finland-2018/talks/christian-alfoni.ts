import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/christian-alfoni";

const talk: Session = {
  people: [speaker],
  title: "Declarative state and side effects",
  description:
    "Writing declarative code for our UIs is so common that we do not even think about it. But when it comes to writing logic for managing state and side effects it requires a lot of discipline to get the same benefits. We are going to talk about how we can get help writing our business logic in a declarative manner and see what benefits it gives us.",
  type: SessionType.TALK,
  keywords: speaker.keywords,
};

export default talk;
