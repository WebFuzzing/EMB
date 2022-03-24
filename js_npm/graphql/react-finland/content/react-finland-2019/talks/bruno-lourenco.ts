import Keyword from "../../../server/schema/keywords";
import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/bruno-lourenco";

const talk: Session = {
  people: [speaker],
  title: "Delightful UI animations by understanding the brain",
  description: `Check out the world’s award-winning websites—what do they all have in common? Animations! They have elements that move and change. When done well, animations can amaze us.
  But why are some animations delightful while others are so annoying? The answer could be in how our brain perceives motion.

  Understanding this could be the key to confidently adding animations to our websites and apps.
  Animations with code seem hard, but they shouldn’t be. In this talk, I will give some tips and explore some tools to simplify creating animations in React.`,
  type: SessionType.TALK,
  keywords: [Keyword.ANIMATION, Keyword.REACT],
  urls: {
    video: "https://www.youtube.com/watch?v=TszRBCc6DQ4",
  },
};

export default talk;
