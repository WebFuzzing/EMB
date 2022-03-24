import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/jeremy-wagner";

const talk: Session = {
  people: [speaker],
  title: "Make It Boring",
  description: `A good case can be made for why boring is preferable to exciting in many aspects of our lives. Yet, we’re reluctant to apply this sentiment to the web. Rather, we’re in a relentless pursuit of the exciting in this increasingly JavaScript-centric world of web development.

  A pursuit that, despite our best efforts, litters the landscape of the web with excruciatingly slow and inaccessible pages. As division brews over the roles of HTML, CSS, and indeed JavaScript itself, we need to ask if it’s worth making the web boring again.

  But why make the web more boring? Because boring is fast, resilient, fault tolerant, and accessible. Boring is the essence of unobtrusive designs that facilitate interactions rather than hinder them. When we rely wholesale on JavaScript to build for the web, we’re inevitably reinventing things—things that can hinder or even fail people in unexpected ways despite our best intentions.

  In this session, you’ll learn why building for the web with a greater emphasis on core technologies is far from boring, but truly rewarding and exciting work that makes the web more inclusive.`,
  type: SessionType.TALK, // SessionType.LIGHTNING_TALK, SessionType.KEYNOTE, SessionType.WORKSHOP are possible too
  keywords: [],
};

export default talk;
