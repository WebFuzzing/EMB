import { Session, SessionType } from "../../../server/schema/Session";
import Keyword from "../../../server/schema/keywords";
import speaker from "../../people/tanmai-gopal";

const talk: Session = {
  people: [speaker],
  title: "Writing a realtime GraphQL backend as a compiler in Haskell",
  description: `At Hasura, we’ve been building a high-performance GraphQL backend that can handle a large number of subscription connections. We took a few interesting approaches to tackle this problem, and in this talk I’ll go over the key architectural decisions, programming constructs and of course the unforeseen implementation pitfalls we ran into. I’ll also cover tools from the Haskell ecosystem that made this possible. These are some of the topics I’ll touch upon:

* Fast GraphQL parsing with parser combinators
* Modelling and manipulating the GraphQL AST with algebraic data types
* Software Transactional Memory: Concurrency constructs for scaling graphql subscriptions
`,
  type: SessionType.LIGHTNING_TALK,
  keywords: [Keyword.GRAPHQL],
  urls: {
    web: "",
    slides: "http://slides.graphql-finland.fi/2018/13-tanmai-gopal.pdf",
    video: "https://www.youtube.com/watch?v=bQLwmYFdCFQ",
  },
};

export default talk;
