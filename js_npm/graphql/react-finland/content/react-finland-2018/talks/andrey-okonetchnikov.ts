import Keyword from "../../../server/schema/keywords";
import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/andrey-okonetchnikov";

const talk: Session = {
  people: [speaker],
  title: "Make linting great again!",
  description: `No other topic in software development probably has so much controversy as linting. 
  
With a wrong workflow linting can be really a pain and will slow you and your team down. With a proper setup, though, it can save you hours of manual work reformatting the code and reducing the code-review overhead.
  
This talk is a quick introduction into how [🚫💩 lint-staged](https://github.com/okonet/lint-staged) — a node.js library I created and maintain — can improve developer experience in historically pita field — linting. 
  
Coupled with tools that analyze and improve the code like ESLint, Stylelint, Prettier and Jest, you'll witness how much difference can a small tool like this make.`,
  type: SessionType.LIGHTNING_TALK,
  keywords: [Keyword.CODE_QUALITY, Keyword.LINTING],
};

export default talk;
