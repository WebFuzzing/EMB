import { Session, SessionType } from "../../../server/schema/Session";
import Keyword from "../../../server/schema/keywords";
import speaker from "../../people/glenn-reyes";

const talk: Session = {
  people: [speaker],
  title: "Building tools for GraphQL",
  description:
    "Adopting GraphQL can be fairly demanding and it takes some time to find the right tooling. What can we do to improve DX and supercharge our GraphQL development? Let's deep-dive into GraphQL tooling that help us building GraphQL at scale.",
  type: SessionType.TALK,
  keywords: [Keyword.GRAPHQL],
  urls: {
    web: "",
    slides: "http://slides.graphql-finland.fi/2018/06-glenn-reyes.pdf",
  },
};

export default talk;
