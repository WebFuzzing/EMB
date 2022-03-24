import { Session, SessionType } from "../../../server/schema/Session";
import { paasitorni } from "../../locations";
import { davidKhourshid } from "../../people";

const workshop: Session = {
  type: SessionType.WORKSHOP,
  people: [davidKhourshid],
  title: "Modeling React Applications with Statecharts",
  description: `Statecharts are a powerful, well-established formalism that describe even the most complex application behavior and logic in a visual, hierarchical, and deterministic way. In this workshop you will learn about finite state machines and statecharts, and apply them to real-life React applications in ways that will increase productivity and eliminate entire classes of possible bugs from your code.  You will also learn how to:

- Refactor React applications of any size to use statecharts, piece by piece
- Auto-generate full integration tests
- Visualize application logic
- Analyze statecharts to determine which user flows can be optimized
- Identify all possible edge cases
- Apply late-breaking changes and requirements methodically
- Auto-generate designs of all possible component states with Storybook
- Use advanced features of XState

> You could clone the [workshop repository](https://github.com/davidkpiano/xstate-react-workshop) before the workshop to speed up the setup.
`,
  keywords: davidKhourshid.keywords,
  location: paasitorni,
};

export default workshop;
