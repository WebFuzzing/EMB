import { Session, SessionType } from "../../../server/schema/Session";
import { elisaAppelsiini } from "../../locations";
import nik from "../../people/nik-graf";
import patrick from "../../people/patrick-ecker";

const workshop: Session = {
  type: SessionType.WORKSHOP,
  people: [patrick, nik],
  title: "Getting Started with ReasonML & ReasonReact",
  description: `ReasonML is a new cool JavaScript-like language with a strong type-system and first level React bindings. As with every new language, there comes a new ecosystem and unfamiliar concepts with it.

In this workshop, attendees will first of all learn how to setup and use ReasonML with React and find explanations on how to find relevant information for their coding problems (navigating the docs, leverage the community).

Later own they will discover the capabilities of the underlying JS-compiler platform called BuckleScript and see how Reason translates to JavaScript. They slowly will get introduced to the interoperability layer to be able to interact with existing JavaScript code and learn how the React component model works in practise.

In more detail, the workshop will cover following topics:

- Why ReasonML?
- Introduction to the platform / tech stack (OCaml, BuckleScript, Tooling, ReasonML)
- Dev-Setup / Editor integration
- Basic language features (data types, pattern-matching, Collection APIs)
- BuckleScript related topics (JS-compilation,
- React related topics (stateless / reducer components, JSX, CSS etc.)
- Interoperability with existing JavaScript

In the end, ambitious attendees will be able to integrate Reason React components in their existing React codebase without interrupting the existing development workflows.`,
  keywords: patrick.keywords,
  location: elisaAppelsiini,
};

export default workshop;
