import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/ville-pelkonen";

const talk: Session = {
  people: [speaker],
  title: "Hacking App Ratings",
  description: `
  React Native or React hybrid mobile apps are more prevalent than ever. Yet, the pitfalls of mobile can take many developers coming from the browser world by surprise. App store ratings are a notoriously impactful metric for every app out there, a metric that’s not easy to push into the higher ranks. Understanding the app store market is easy enough, but the industry secret lies in making a consistent rating system that hacks deep into the end user’s mind using psychology and great user experience – all in an ethical way. Learn the principles of how to get that 2,7 average star rating into 4,2 stars in no longer than two weeks, and fully in JS.`,
  type: SessionType.LIGHTNING_TALK,
  keywords: [],
};

export default talk;
