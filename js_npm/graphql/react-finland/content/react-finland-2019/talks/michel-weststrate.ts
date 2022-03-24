import Keyword from "../../../server/schema/keywords";
import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/michel-weststrate";

const talk: Session = {
  people: [speaker],
  title: "MobX - The Journey",
  description: `
MobX is downloaded a million times per month, and used by many large and small companies, such as Microsoft, Amazon, Lyft, SAP and DICE.

By now, it is 3,5 years ago (in other words, ancient!) that I've wrote it and started building a community around it. In this talk I will both reflect on the product and my journey into the Open Source Software world:

- What makes a great open source library? (Can we even answer that question)
- What is so appealing about the programming model of MobX? (Reactive programming, yet, not scary?)
- How is it possible that open source even works. What does it mean to be a good OSS citizen? (Yeah, I'm looking at you!)
- Why do so many maintainers burn out? (And how to avoid that)

Reflection on the past often gives clues about the future:

- What is the future of MobX and client side state management?
- And what is the future of open source? How can we foster innovation. And maintenance?

Expect a highly personal talk about a successful open source project. 
Some first hand experiences from an author that still tries to figure out how much of all that is just dumb luck in the end :).
`,
  type: SessionType.KEYNOTE,
  keywords: [
    Keyword.MOBX,
    Keyword.MOBX_STATE_TREE,
    Keyword.OPEN_SOURCE,
    Keyword.REACT,
  ],
  urls: {
    slides: "https://slides.react-finland.fi/2019/michel-weststrate.pdf",
    web: "https://docs.google.com/presentation/d/12Dd8iYxcA8Wl2yaOE_FNb-MaB7cktUE82JFXVlrO-6U/edit#slide=id.p",
    video: "https://www.youtube.com/watch?v=MIh1qSHi_Fc",
  },
};

export default talk;
