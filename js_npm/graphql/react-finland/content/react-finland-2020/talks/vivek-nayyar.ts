import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/vivek-nayyar";

const talk: Session = {
  people: [speaker],
  title: "Upgrading your legacy code incrementally with codemods",
  description: `
How many times has it happened that a new version of a library is released and some of the APIs have either been deprecated or been marked unsafe or at times removed completely. If you have upgraded React from v14 to v16 or styled-components, you understand the issue. JavaScript itself changes over time as well and ideally you'll adapt to the improvements made to the language.

The ongoing change is a challenge to web developers and in this talk we'll focus on how to use JavaScript itself to keep a codebase up to date by using the power of codemods. We'll use an open source framework, jscodeshift, to accomplish this and we'll go through their pros/cons as we become accustomed to their usage.
  `,
  type: SessionType.TALK,
  keywords: [],
};

export default talk;
