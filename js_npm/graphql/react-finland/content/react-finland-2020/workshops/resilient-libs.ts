import Keyword from "../../../server/schema/keywords";
import { Session, SessionType } from "../../../server/schema/Session";
import { internet } from "../../locations";
import andrey from "../../people/andrey-okonetchnikov";
import artem from "../../people/artem-sapegin";

const workshop: Session = {
  type: SessionType.WORKSHOP,
  people: [andrey, artem],
  title: "Resilient Component Libraries with React",
  description: `In the world where frontend applications are becoming more complex and time constraints more tight, design systems and component libraries are becoming everyday tools for developers to fulfil new business requirements as fast as possible without sacrificing user experience and quality of the product. 

  The truth is, *the way your design system and component library is built will inevitably affect the quality of the application code*. If components aren't flexible enough to match business requirements and time constraints, there will be hacks and workarounds — the source of endless bugs and maintenance issues. On the other hand, too much control over markup and styling can lead to inconsistent user interfaces and hurt user experience.
    
  This workshop covers general architecture principles and React-specific techniques that lead to better components architecture and API design and are going to make teams be more productive and products be more consistent.
  
  The workshop takes place online 27.05 11:00-18:00 (Finnish time).`,
  keywords: [
    Keyword.REACT,
    Keyword.STYLING,
    Keyword.STYLE_GUIDES,
    Keyword.TOOLING,
  ],
  location: internet,
};

export default workshop;
