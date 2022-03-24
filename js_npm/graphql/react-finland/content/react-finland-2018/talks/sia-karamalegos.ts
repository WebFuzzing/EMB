import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/sia-karamalegos";

const talk: Session = {
  people: [speaker],
  title: "Universal React Apps Using Next.js",
  description:
    "Every user’s hardware is different, and processing speed can hinder user experience on client-side rendered React applications. Server-side rendering and code-splitting can drastically improve user experience by minimizing the work that the client has to do.\n\nIt’s easy to get lost in the lingo, so come learn what it all means and how to easily build universal React apps using the Next.js framework. We’ll walk through the concepts and use code examples to cement your understanding. You’ll get the most out of this session if you’re comfortable with React and ES6 syntax.",
  type: SessionType.TALK,
  keywords: speaker.keywords,
  urls: {
    web: "https://siakaramalegos.github.io/nextjs-talk-slides/",
  },
};

export default talk;
