import { Session, SessionType } from "../../../server/schema/Session";
import { elisaAppelsiini } from "../../locations";
import sven from "../../people/sven-sauleau";

const workshop: Session = {
  type: SessionType.WORKSHOP,
  people: [sven],
  title: "Babel",
  description: `
You will learn how to refactor your React code effectively in this workshop by Sven Sauleau, one of the core developers of Babel. He will discuss the following topics:

* How and why JSX syntax works?
* How to create your first Babel plugin?
* How to perform type analysis?

[Read your own refactoring tool](https://www.xtuc.fr/notes/your-own-refactoring-tool.html) article to learn more!
`,
  keywords: sven.keywords,
  location: elisaAppelsiini,
};

export default workshop;
