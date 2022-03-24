import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/kasia-jastrzebska";

const talk: Session = {
  people: [speaker],
  title: "Styled Components, SSR, and Theming",
  description:
    "All you need to know to become hero of CSS-in-JS with `styled-components`. We will go through the new API, performance improvements, server side rendering with Next.js and the theming manager available with v2 of `styled-components`.",
  type: SessionType.TALK,
  keywords: speaker.keywords,
};

export default talk;
