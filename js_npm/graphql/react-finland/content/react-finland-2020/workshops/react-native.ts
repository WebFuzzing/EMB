import { Session, SessionType } from "../../../server/schema/Session";
import { internet } from "../../locations";
import { juhaLinnanen, naderDabit } from "../../people";

const workshop: Session = {
  type: SessionType.WORKSHOP,
  people: [naderDabit, juhaLinnanen],
  title: "React Native",
  description: `In this workshop you will learn everything needed to create cross-platform mobile applications for iOS and Android. We will show you how to leverage your possible existing Javascript and React skills and put them in good use creating beautiful mobile apps. Workshop will cover the core features of React Native, which will give you a solid foundation to work on your own afterwards. The workshop will be hands on, creating an example application, where you get to learn first hand the concepts being taught, all the while having experienced trainers ready to help you progress fast. 
 
Pre-requisites: Basic Javascript (ES6) / React knowledge, macOS preferred (ability to build for iOS), tools installed`,
  keywords: [],
  location: internet,
};

export default workshop;
