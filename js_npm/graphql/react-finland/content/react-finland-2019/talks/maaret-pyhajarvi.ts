import Keyword from "../../../server/schema/keywords";
import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/maaret-pyhajarvi";

const talk: Session = {
  people: [speaker],
  title: "Intersection of Automation and Exploratory Testing",
  description: `I’m using exploratory testing to design which tests I leave behind as automated. Creating automation forces me to explore details in a natural way. When an automated test fails, it is an invitation to explore. The two sides of testing, automation and exploration, complement each other. These intertwine the considerations of the best for today and for the future. 

For great testing bringing value now as well as when we are not around, we need to be great at testing - uncovering relevant information - and programming - building maintainable test systems. At the core of all this is learning. With our industry doubling in size every five years, half of us have less than five years of experience. We all start somewhere on our learning journey. 

In this talk, we look at skills-focused path to better testing in the intersection of automation and exploratory testing. We can arrive at the intersection by enhancing our individual skills, or our collaboration skills. What could you do to become one of those testers who companies seek after that work well in the intersection, giving up the false dichotomy? 

Takeaways:

- recognizing skill in testing and skill in programming for testing as dimensions
- building skills in testing / programming
- using collaboration to achieve better testing`,
  type: SessionType.TALK,
  keywords: [Keyword.REACT, Keyword.TESTING],
  urls: {
    slides: "https://slides.react-finland.fi/2019/maaret-pyhajarvi.pdf",
    web: "https://www.slideshare.net/maaretp/react-finland-intersection-of-automation-and-exploratory-testing",
    video: "https://www.youtube.com/watch?v=Dg4JawCeNhs",
  },
};

export default talk;
