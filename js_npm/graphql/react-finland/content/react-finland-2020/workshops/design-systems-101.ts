import Keyword from "../../../server/schema/keywords";
import { Session, SessionType } from "../../../server/schema/Session";
import { internet } from "../../locations";
import andrey from "../../people/andrey-okonetchnikov";
import varya from "../../people/varya-stepanova";

const workshop: Session = {
  type: SessionType.WORKSHOP,
  people: [andrey, varya],
  title: "Design Systems 101",
  description: `Ever wondered what is a design system and how is it different to a component library? Or how to introduce a design system to your product or organisation? This one day workshop will help your multi-disciplinary team of designers, developers, and project managers to get familiar with design systems, find design-related issues in your product, and understand how to work around them by improving product-related communication. 

During this workshop participants will get a hands-on experience with basic design processes while creating and maintaining their own design system. You'll learn how following these processes helps with establishing a well documented universal design language which reflects a shared understanding of the brand and translates into the design-system implementation in code and design tools. 
  
With the knowledge of communicational practises and modern design and developer's tools you too should be able to introduce a design system in your company and star working smarter, not harder.

The workshop takes place online 25.05 11:00-18:00 (Finnish time).`,
  keywords: [Keyword.STYLE_GUIDES],
  location: internet,
};

export default workshop;
