import Keyword from "../../../server/schema/keywords";
import { Session, SessionType } from "../../../server/schema/Session";
import { paasitorni } from "../../locations";
import andrey from "../../people/andrey-okonetchnikov";
import artem from "../../people/artem-sapegin";

const workshop: Session = {
  type: SessionType.WORKSHOP,
  people: [andrey, artem],
  title: "Design Systems for React Developers",
  description: `Development of digital products is a complex process that involves multiple stakeholders and roles: managers, designers, engineers, QA, and customers. This increases the communication overhead, slows down the process, and oftentimes results and less than ideal user experience.

- How to communicate design ideas between different stakeholders effectively?
- How to deliver consistent UI without constant supervising?
- How to ensure same problems not being solved multiple times?

Design systems offer a systematic approach to the process of product creation. This approach establishes an organization-wide design language that can be backed into a UI library and the styleguide and becomes the single source of truth for the entire organization. Component libraries and living styleguides encourage reuse, promote consistency, improve team productivity, help to identify common patterns and solve similar problems only once.

This workshop will show you how to:

  - Understand what a design system is and how to start thinking in design constraints
  - Analyze and split your UI into small reusable components
  - Setup an environment to create components in isolation
  - Create a living style guide and start building a component library
  - Structure your application around components
  - Write maintainable styles for your components based on the design system
 
> Before coming to the workshop, make sure you set up [component-driven-development repository](https://github.com/component-driven/component-driven-development) and follow the instructions. Doing this will save time and help you get started faster.`,
  keywords: [
    Keyword.REACT,
    Keyword.STYLING,
    Keyword.STYLE_GUIDES,
    Keyword.TOOLING,
  ],
  location: paasitorni,
};

export default workshop;
