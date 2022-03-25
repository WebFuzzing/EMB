import { Schedule } from "../../server/schema/Schedule";
import { Level, SessionType } from "../../server/schema/Session";
import * as locations from "../locations";
import * as people from "../people";

// All times are given in GMT+0
const monday: Schedule = {
  day: "2021-08-30",
  description: "Monday",
  location: locations.internet,
  intervals: [
    /*{
      begin: "11:00",
      end: "15:00",
      sessions: [
        {
          type: SessionType.WORKSHOP,
          level: Level.INTERMEDIATE,
          title: "Modeling React Apps with XState",
          description:
            "In this workshop, you will become familiar with XState and learn to apply it in your daily work. If you have ever wanted to pick up state machines and statecharts, this is a good chance to do it.",
          people: [people.davidKhourshid],
        },
      ],
    },
    {
      begin: "12:00",
      end: "15:00",
      sessions: [
        {
          type: SessionType.WORKSHOP,
          level: Level.BEGINNER,
          title: "Video on the web",
          description: `Video is one of the fastest growing mediums on the web and in mobile applications. Video files have been shown to increase engagement, and can be a great way to deliver your message quickly. (And who doesn’t love animated GIFs?)

However, video that takes a long time to start up leads to frustration and abandonment. The same goes for video that stalls during playback. In this workshop, you’ll learn best practices to optimize the delivery of your video to you customers, ensuring fast delivery and minimizing stalls for a great customer experience.`,
          people: [people.doug],
        },
      ],
    },
    {
      begin: "14:00",
      end: "17:00",
      sessions: [
        {
          type: SessionType.WORKSHOP,
          level: Level.INTERMEDIATE,
          title: "Testing React Native Apps Using Cypress",
          description:
            "How to run React Native apps in the browser using react-native-web and Expo, then how to run end-to-end tests with Cypress. We can add code coverage reporting and executing tests on CI, giving us high confidence in our application's code. Finally, we will learn how to mock the native components during tests.",
          people: [people.gleb],
        },
      ],
    },*/
    {
      begin: "16:00",
      end: "18:00",
      title: "Opening",
      mc: people.juhoVepsalainen,
      sessions: [
        {
          type: SessionType.TALK,
          level: Level.BEGINNER,
          title: "Breaking out of the confines: making games in React",
          description:
            "To show how we can make rich, interactive experiences, for instance games, with React semantics and shareable components, and what that means for traditional constructs, like the render-loop.",
          people: [people.paul],
          urls: {
            video: "https://www.youtube.com/watch?v=CLTZrFmri2E",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "The State of XState",
          description:
            "Over the past few years, state machines, statecharts, and the actor model have proven to be viable concepts for building complex application logic in a clear, visual way with XState. In this talk, we'll take a peek into the future of XState, including new features in the next version, and new tools and services that will make it even easier to create and collaborate on state machines.",
          people: [people.davidKhourshid],
          urls: {
            web: "https://slides.com/davidkhourshid/state-of-xstate",
            video: "https://www.youtube.com/watch?v=Q05jcu9N98g",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "SolidJS - Reactive JSX",
          description:
            "An introduction to SolidJS UI Library. Explore an example to show the similarity and differences between Hook + Virtual DOM versus Reactivity + DOM.",
          people: [people.ryan],
          urls: {
            web: "https://docs.google.com/presentation/d/1dPJfjkuzNikQ9taj-aF0XwtV2EhcdcEh7tk0y6SYXMY/edit?usp=sharing",
            video: "https://www.youtube.com/watch?v=2iK9zzhSKo4",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "JSX for Designers",
          description:
            "Even today, with advanced tooling and frameworks, the gap between design and development still exists. This talk will examine how we can eliminate handoff between design and development teams by using JSX as a shared source of truth across any platform.",
          people: [people.travis],
          urls: {
            web: "https://jsxui.com/presentation",
            video: "https://www.youtube.com/watch?v=RGQR79PbTFU",
          },
        },
      ],
      urls: {
        video: "https://www.youtube.com/watch?v=vpeO0J7FXAE",
      },
    },
  ],
};

const tuesday: Schedule = {
  day: "2021-08-31",
  description: "Tuesday",
  location: locations.internet,
  intervals: [
    {
      begin: "09:00",
      end: "12:00",
      sessions: [
        {
          type: SessionType.WORKSHOP,
          level: Level.ADVANCED,
          title:
            "Current state of SSR in React, its limitations and its future",
          description: `Server Side Rendering (SSR) can be achieved in many ways. renderToString and renderToNodeStream are the basic functionality exported by React. But how can they be integrated into the current app and where are limitations of renderToNodeStream. We will learn how code splitting in SSR with lazy works and how the experimental Suspense in React 18 would change existing 3rd-party-libraries. When there is time we dive into Next.js and Gatsby, its differences and how to achieve a simple website with one of these libraries. When there is even more time (might not be the case) we will talk about React’s server components which are yet in research.

The goal of this workshop is to understand how SSR works in React and how to improve it.

**Sold out!**`,
          people: [people.jan],
        },
      ],
    },
    {
      begin: "10:00",
      end: "13:00",
      sessions: [
        {
          type: SessionType.WORKSHOP,
          level: Level.INTERMEDIATE,
          title: "Powering Next.js with Drupal",
          description: `This session will cover why and how to fully decouple Drupal with Next.JS, an open source modern frontend framework that will empower frontend developers to build incredibly fast and interactive websites using React without losing key benefits that Drupal provides like content modeling and administration UI.

Join us if you want to discover the ins and outs of how Drupal and Next.js ecosystem complement each other.

**Sold out!**`,
          people: [people.davidC, people.mario],
        },
      ],
    },
    /*
    {
      begin: "10:00",
      end: "14:00",
      sessions: [
        {
          type: SessionType.WORKSHOP,
          level: Level.BEGINNER,
          title: "MicroFrontends in React",
          description: `Our first MicroFrontend project started almost to the day exactly five years ago and since then we have accompanied countless projects following this architectural style. Ever since, the technical possibilities with which MicroFrontends can be implemented have changed significantly and the options have increased considerably.

On the client-side we came from dilettante approaches based on iFrames and settled up with more elaborated strategies like Webpack Module-Federation or EcmaScript Import-Maps. We moved from pure Edge-Side-Includes to full fletched frameworks that allow us to compose MicroFrontends more comfortably and sustainably on the server-side. And even during the implementation, we achieved entirely new possibilities by more mature ways to manage MonoRepos and distributed or incremental builds. By showing real-world examples, we will go into detail about the evolution of these different approaches.

Upon a closer look at the challenges that still remain, it will become clear that none of these new concepts and tools come for free but with a fair amount of newly introduced complexity. Ultimately, this workshop will explain the different possibilities to implement MicroFrontends in the past, the present, and the future. More importantly, it will leave you to know which of the available approaches is best suited for your specific task at hand.`,
          people: [people.david],
        },
      ],
    },
    */
    {
      begin: "15:00",
      end: "16:30",
      title: "Architecture",
      mc: people.juhoVepsalainen,
      sessions: [
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "Hooks are a great abstraction model",
          description: `The discussion explores the separation of concerns between logic (Model) and UI (View) and how React hooks are a great abstraction model that help achieve that.

We'll start with designing a component's API (in a top down approach), then dive deeper to explore how to implement this API, while maintaining a good separation of concerns.

We'll create custom React hooks to implement the Model and React components for the UI. Both expose self-documenting APIs and are completely decoupled from each other.

To demo this approach, we'll create a simple Toggler component (a UI component that allows going back and forth in an array of values).

Towards the end, we'll explore the benefits, focusing on testing these decoupled units in isolation.`,
          people: [people.calin],
          urls: {
            video: "https://www.youtube.com/watch?v=G_-KaHOeuu4",
            demo: "https://github.com/calintamas/talks/tree/main/2021-react-finland/react-native-toggler",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "Web Components in React",
          description:
            "Likely you've heard of Web Components but do you know how to use them with React? If not, then this is the demonstration for you!",
          people: [people.matias],
          urls: {
            video: "https://www.youtube.com/watch?v=fgtS_nfMOtw",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "Micro Frontends in React — a State of the Art Approach",
          description:
            "David will explain the different possibilities to implement MicroFrontends in the past, the present, and the future. More importantly, he will leave you to know which of the available approaches is best suited for your specific task at hand.",
          people: [people.david],
          urls: {
            video: "https://www.youtube.com/watch?v=WkHJqJ-IyK8",
          },
        },
      ],
      urls: {
        video: "https://www.youtube.com/watch?v=8ZEsdcnHofo",
      },
    },
  ],
};

const wednesday: Schedule = {
  day: "2021-09-01",
  description: "Wednesday",
  location: locations.internet,
  intervals: [
    {
      begin: "07:00",
      end: "08:30",
      title: "Inclusivity",
      mc: people.manjula,
      sessions: [
        {
          type: SessionType.TALK,
          level: Level.BEGINNER,
          title: "Reasons why we need inclusion",
          description: ``,
          people: [people.nasim],
          urls: {
            slides:
              "https://drive.google.com/file/d/1dv0ZOFDTitrUHJfJAgKs6YiO4M8Fxwcs/view?usp=sharing",
            video: "https://www.youtube.com/watch?v=j91tmdeObDU",
          },
        },
        /*{
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "Understanding unconscious bias",
          description: ``,
          people: [people.kristopher],
        },*/
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "Structural Racism",
          description: `How our narrow understanding of racism (visible and interpersonal level) still stands in the way of racial equity and inclusion (in tech, too) and how racial bias can be seen in tech, products etc.`,
          people: [people.jasmin],
          /*urls: {
            slides:
              "https://drive.google.com/file/d/1qH2RQo_DIy-1bre1BSJ5wl_E3o4MLiqV/view?usp=sharing",
            video: "https://www.youtube.com/watch?v=6jLVAX6UZhs",
          },*/
        },
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "How to avoid bias or exclusion when recruiting",
          description:
            "I focus on why inclusion is important in hiring and what are the inclusive hiring practices for any organization that could be taken into use. Inclusive hiring and evaluation process are the great window of opportunity that can truly support inclusion with practical actions. I offer practical tips and a way to implement small changes while working the way towards more inclusive hiring culture and strategy.",
          people: [people.emmi],
          urls: {
            slides:
              "https://drive.google.com/file/d/1ck8RYwhNMpvqg2zYdITUl4Gbn-yR_219/view?usp=sharing",
            video: "https://www.youtube.com/watch?v=JdXzotdglao",
          },
        },
      ],
      urls: {
        video: "https://www.youtube.com/watch?v=4CAMFBkpOjE",
      },
    },
    {
      begin: "10:00",
      end: "11:30",
      title: "Styling",
      mc: people.manjula,
      sessions: [
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "Zero-runtime CSS-in-TypeScript with vanilla-extract",
          description:
            "Can we have themeable CSS-in-TypeScript without the runtime cost? In this talk we'll have a quick look at how this can be achieved with vanilla-extract.",
          people: [people.mark],
          urls: {
            video: "https://www.youtube.com/watch?v=23VqED_kO2Q",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "Rethinking CSS - Introducing Stylex",
          description:
            "CSS + superpowers - bloat. How Stylex creates a zero-cost abstraction that gives CSS superpowers.",
          people: [people.naman],
          urls: {
            video: "https://www.youtube.com/watch?v=ur-sGzUWId4",
            slides:
              "https://drive.google.com/file/d/1jfbB2e-aZjCHqdYOZR9xC2p48YFLULgN/view?usp=sharing",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "A thorough analysis of CSS-in-JS",
          description: `There are two mutually exclusive methods that CSS-in-JS libraries use to generate and ship styles to the browser. Both methods have benefits and downsides, so let’s analyze them in detail from the loading performance perspective.`,
          people: [people.andrei],
          urls: {
            video: "https://www.youtube.com/watch?v=Nku71cWwZts",
            slides:
              "https://drive.google.com/file/d/1cPHBjEB04rO5Bz0t654gcxT3DLwzDXW1/view?usp=sharing",
          },
        },
      ],
      urls: {
        video: "https://www.youtube.com/watch?v=wIjlBRW4Ty8",
      },
    },
    {
      begin: "12:00",
      end: "13:30",
      title: "Content management",
      mc: people.juhoVepsalainen,
      sessions: [
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "How to View and React without a Head",
          description: `You might have heard about Headless CMS. This new type of content platform gives us some benefits compared to a Monolithic CMS.

We will talk about the key features of any Headless CMS and how we can integrate a headless platform into our React applications. We will focus on Storyblok, a headless CMS that offers a real-time Visual Editor, a great feature for developers and content creators.`,
          people: [people.facundo],
          urls: {
            video: "https://www.youtube.com/watch?v=GRz0ibU2PIk",
            web: "https://slides.com/storyblok/react-without-head-finland",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.BEGINNER,
          title:
            "React Bricks: a CMS with visual editing based on React components",
          description: `Headless CMSs are great for developers, but not for content creators.

React Bricks is the first CMS that is super-great for Developers (it's just React, backed by solid APIs), for Content Creators (it keeps the visual editing experience of no-code tools like Wix) and for Designers (you can express your exact design system and be sure nobody will break it). I'll show you how it works!`,
          people: [people.matteo],
          urls: {
            slides:
              "https://drive.google.com/file/d/1FTcj5EEzIggnlPKHFXhcZ2jOBp3EmYEX/view?usp=sharing",
            video: "https://www.youtube.com/watch?v=lzR5Wndfy4s",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "Contentful and headless",
          description: `How Contentful helps you to manage all the data in a headless e-commerce website.`,
          people: [people.nidhi],
          urls: {
            slides:
              "https://drive.google.com/file/d/1vQd2j8FNPJe9CbznA96Z9_WgTOKM5mlx/view?usp=sharing",
            video: "https://www.youtube.com/watch?v=WbQsJa7-MdA",
          },
        },
      ],
      urls: {
        video: "https://www.youtube.com/watch?v=myzAXCye-Qo",
      },
    },
    {
      begin: "14:00",
      end: "15:30",
      title: "State machines",
      mc: people.juhoVepsalainen,
      sessions: [
        {
          type: SessionType.TALK,
          level: Level.BEGINNER,
          title: "Introducing state machines and statecharts",
          description: `State machines and statecharts can seem intimidating. Especially if you (like me!) didn’t study computer science, aren’t big into maths, or just haven’t come across state machines and statecharts before.

In this session, you’ll get a whirlwind introduction to state machines and statecharts, no prior knowledge and no coding experience required. Are you already familiar with state machines and statecharts but want to get a better understanding of the benefits and how to convince your team to get onboard? That’ll be covered too.

You could be a developer, designer, project manager, multi-disciplinarian or fancy specialist, I believe everyone can get something out of this talk, so join me!`,
          people: [people.laurak],
          urls: {
            video: "https://www.youtube.com/watch?v=Y_lOg-rsqBM",
            web: "https://noti.st/laurakalbag/PPsNij/introducting-state-machines-and-statecharts",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "Make legacy code delightful with statecharts",
          description:
            "If you must have legacy code, you want it written in statecharts. We'll break down the mental models required for understanding unfamiliar UI code, and compare the maintenance costs with and without statecharts.",
          people: [people.matt],
          urls: {
            video: "https://www.youtube.com/watch?v=zll9uDQOOq0",
            web: "https://slides.com/mattpocock/make-legacy-code-delightful-with-statecharts",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "The Actor Model: a new mental model for React",
          description: `System-level state management in modern React applications is challenging. Usually, as the applications grow, the complexity of interaction between different parts of the application compounds.

The more you need those isolated parts to talk, the higher the level of complexity. Usually, the common pitfalls of these communications are underestimated especially with the recent trend of State management that tends to keep the application state outside of React and distributed.

In this talk, we'll see how the Actor Model architecture can come to the rescue and what makes it stand out compared to the other approaches.`,
          people: [people.farzadYz],
          urls: {
            video: "https://www.youtube.com/watch?v=9k1ZHHJWt7k",
            web: "https://slides.com/farzadyz/actor-model-rf2021",
          },
        },
      ],
      urls: {
        video: "https://www.youtube.com/watch?v=NmVZd-VWu3I",
      },
    },
    {
      begin: "16:00",
      end: "17:30",
      title: "GraphQL",
      mc: people.juhoVepsalainen,
      sessions: [
        {
          type: SessionType.TALK,
          level: Level.BEGINNER,
          title: "Automate your stack with GraphQL",
          description: `Lukas will demonstrate how you can have your backend GraphQL in sync with your frontend code on a framework agnostic way.

- Having the advantage of autocompletion while writing the GraphQL queries by IDE support
- Built in type safety through static schemas
- Automatic generated frontend code for your framework of choice (React, Vue.js, Angular)
- Having everything rebuild through a built in watch mode`,
          people: [people.lukas],
          urls: {
            demo: "https://github.com/lukasholzer/react-finland-2021",
            video: "https://www.youtube.com/watch?v=Rt2IcqpdbzE",
            slides:
              "https://drive.google.com/file/d/1zypcpu803ZEyvqIimvL2sJk2gOY4BpbZ/view?usp=sharing",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "How to cache GraphQL queries at the edge",
          description:
            "A demo and deep dive into GraphCDN, the edge caching GraphQL CDN.",
          people: [people.max],
          urls: {
            video: "https://www.youtube.com/watch?v=EjrJtp4JaGQ",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title:
            "Using Apollo Federation in a microservice architecture in Kubernetes",
          description:
            "Being flexible with Apollo Federation and autoscaling with Kubernetes opens many opportunities to scale your app. Jan will show how these two services work hand in hand and why autoscaling matters.",
          people: [people.jan],
          urls: {
            demo: "https://github.com/JPeer264/react-finland-2021",
            video: "https://www.youtube.com/watch?v=jTRFpjKKxmA",
          },
        },
      ],
      urls: {
        video: "https://www.youtube.com/watch?v=bph1c8qNY0g",
      },
    },
  ],
};

const thursday: Schedule = {
  day: "2021-09-02",
  description: "Thursday",
  location: locations.internet,
  intervals: [
    {
      begin: "07:00",
      end: "08:30",
      title: "Techniques I",
      mc: people.manjula,
      sessions: [
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "How to Delegate your React Dialogs",
          description:
            "How to properly hide modals / dialogs / drawers functionalities behind buttons that spawn them without cluttering container components.",
          people: [people.calinb],
          urls: {
            video: "https://www.youtube.com/watch?v=SIVKJrPHV-Y",
            demo: "https://github.com/calinbogdan/delegate-your-react-dialogs",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title:
            "Testing the integrity of your React components by publishing in a private registry",
          description:
            "The final stage of a React component is when it is being published and distributed. How can I ensure my packages won’t crash in production? This talk will help you to test your React components by publishing them to a private registry and running End-to-End tests against them.",
          people: [people.juan],
          urls: {
            video: "https://www.youtube.com/watch?v=bRKZbrlQqLY",
            demo: "https://github.com/juanpicado/verdaccio-end-to-end-tests",
            web: "https://docs.google.com/presentation/d/1a2xkqj1KlUayR1Bva1bVYvavwOPVuLplxFtup9MI_U4/edit?usp=sharing",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "The Eternal Sunshine of the Zero Build Pipeline",
          description: `For many years, we have migrated all our devtools to Node.js for the sake of simplicity: a common language (JS/TS), a large ecosystem (NPM), and a powerful engine. In the meantime, we moved a lot of computation tasks to the client-side thanks to PWA and JavaScript Hegemony.

So we made Webapps for years, developing with awesome reactive frameworks and bundling a lot of dependencies. We progressively moved from our simplicity to complex apps toolchains. We've become the new Java-like ecosystem. It sucks.

It's 2021, we've got a lot of new technologies to sustain our Users eXperience. It's time to have a break and rethink our tools rather than going faster and faster in the same direction. It's time to redesign the Developer eXperience. It's time for a bundle-free dev environment. It's time to embrace a new frontend building philosophy, still with our lovely JavaScript.

Introducing Snowpack, Vite, Astro, and other Bare Modules tools concepts!`,
          people: [people.m4Dz],
          urls: {
            video: "https://www.youtube.com/watch?v=oUV2kZedlLU",
          },
        },
      ],
      urls: {
        video: "https://www.youtube.com/watch?v=5olfi5wbgF4",
      },
    },
    {
      begin: "10:00",
      end: "11:30",
      title: "Frameworks",
      mc: people.manjula,
      sessions: [
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "Aleph.js - Build your React app in Deno",
          description:
            "A demo on Aleph.js about good development experience and deno runtime.",
          people: [people.je],
          urls: {
            video: "https://www.youtube.com/watch?v=OY7Cp79x38o",
            web: "https://www.figma.com/file/C3y0F2Yu5HXxbs74uhd7n1/9-2?node-id=0%3A1",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "SSR & SSG in NextJS",
          description:
            "In this talk we are going to have a look on how to use Server-Side-Rendering and Static-Site-Generation in NextJS to serve pages.",
          people: [people.nur],
          urls: {
            video: "https://www.youtube.com/watch?v=UKiyA_GENns",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.BEGINNER,
          title: "Brief introduction to Nuxt",
          description: `In this brief introduction of Nuxt, we will learn Nuxt basics and how to build Git-files based content management system using Nuxt.js and its module system.`,
          people: [people.krutie],
          urls: {
            video: "https://www.youtube.com/watch?v=HrbyeVps-NA",
          },
        },
      ],
      urls: {
        video: "https://www.youtube.com/watch?v=0bU-Y5ailzY",
      },
    },
    {
      begin: "12:00",
      end: "13:30",
      title: "Documentation",
      mc: people.saraVieira,
      sessions: [
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "Opensource Documentation—Tales from React and React Native",
          description:
            "Documentation is often your community's first point of contact with your project and their daily companion at work. So why is documentation the last thing that gets done, and how can we do it better? This talk shares how important documentation is for React and React Native and how you can invest in or contribute to making your favourite project's docs to build a thriving community.",
          people: [people.rachelNabors],
          urls: {
            video: "https://www.youtube.com/watch?v=SgM2lEjCyAY",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "Documenting components with stories",
          description: `Most documentation systems focus on text content of one form or another: WYSIWYG editors, markdown, code comments, and so forth. Storybook, the industry-standard component workshop, takes a very different approach, focusing instead on component examples, or **stories**.

In this demo, I will introduce an open format called Component Story Format (CSF).

I will show how CSF can be used used to create interactive docs in Storybook, including auto-generated DocsPage and freeform MDX documentation. Storybook Docs is a convenient way to build a living production design system.

I will then show how CSF stories can be used create novel forms of documentation, such as multiplayer collaborative docs, interactive design prototypes, and even behavioral documentation via tests.

Finally, I will present the current status and outline a roadmap of improvements that are on their way in the coming months.`,
          people: [people.michael],
          urls: {
            video: "https://www.youtube.com/watch?v=CddIHkbSTm8",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "The Story of ReScript's Documentation Platform",
          description:
            "In this talk, Patrick goes through the effort that went into ReScript's documentation platform.",
          people: [people.patrickEcker],
          urls: {
            slides:
              "https://drive.google.com/file/d/1Nc9m2BpEvzcgUBhDFn48e5xGMQN8iGmm/view?usp=sharing",
            video: "https://www.youtube.com/watch?v=NtlxHUqY0h0",
          },
        },
      ],
      urls: {
        video: "https://www.youtube.com/watch?v=m_ZE-q7z82Y",
      },
    },
    {
      begin: "15:00",
      end: "16:30",
      title: "Accessibility",
      mc: people.saraVieira,
      sessions: [
        {
          type: SessionType.TALK,
          level: Level.BEGINNER,
          title:
            "How to Catch Low-Hanging Accessibility Fruits while Developing",
          description: `We, as developers, tend to develop sites for users like ourselves, which usually causes accessibility problems. As we are often sighted, can use a mouse, and have good fine motor skills, we might (unconsciously) forget those users who aren't like us.

In this demo, I will show some techniques every developer should know and use when developing their sites or apps. These simple checks don't ensure 100% accessibility but help catch the most obvious flaws in your site.`,
          people: [people.eevis],
          urls: {
            video: "https://www.youtube.com/watch?v=CVjbrvsaJDw",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.BEGINNER,
          title: "Continuous Localization in Enterprise Web Projects",
          description: `Did you know that 50% of users will NOT use the English version of your application, if it supports multiple languages? For ambitious high-growth businesses, going global is not "if" but "when" so it's important to recognize the importance of localization, especially in large multi-team projects. This discussion will explore the depths of localization in Agile companies and what can be achieved with a Continuous Localization system. I'll share some common issues we faced with multi-language products and how localization automation helped us to scale the process across many product teams within the company.`,
          people: [people.ante],
          urls: {
            slides:
              "https://drive.google.com/file/d/11X8iSIliBbcT31WSsBg1ciEwWC2rMQeo/view?usp=sharing",
            video: "https://www.youtube.com/watch?v=joQUzBzLEJg",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title:
            "The Digital Accessibility Legal Landscape – why it matters for developers ",
          description:
            "Digital accessibility is a human right of people with disabilities around the globe.  That means developers have an important role to play in advancing human rights!  Join U.S. disability rights lawyer Lainey Feingold for a practical interactive discussion about laws and policies around the globe that impact digital accessibility. Come learn best practices for digital accessibility and how we all can “put the law in our pocket” to help make the digital world inclusive for everyone.",
          people: [people.lainey],
          urls: {
            video: "https://www.youtube.com/watch?v=3by87QbBc0c",
            web: "https://drive.google.com/file/d/1ZaUahv0squI9nfrXKTGHFKJHZ_siXGT0/view?usp=sharing",
          },
        },
      ],
      urls: {
        video: "https://www.youtube.com/watch?v=FjERb0PFkJc",
      },
    },
  ],
};

const friday: Schedule = {
  day: "2021-09-03",
  description: "Friday",
  location: locations.internet,
  intervals: [
    {
      begin: "07:00",
      end: "08:30",
      title: "React Native and Testing",
      mc: people.saraVieira,
      sessions: [
        {
          type: SessionType.TALK,
          level: Level.BEGINNER,
          title: "Screenshot testing with ViteShot",
          description:
            "François will introduce ViteShot, a new open-source tool that can generate screenshots of HTML/CSS components within seconds, so you can be confident that your UI looks exactly the way it's supposed to.",
          people: [people.francois],
          urls: {
            video: "https://www.youtube.com/watch?v=Ag5NBguCucc",
            web: "https://www.canva.com/design/DAEovvDFRYo/RVl-IcjckNu8uM0z2VuWgA/watch",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "Integration Testing for React Native Apps",
          description:
            "My unpopular opinion is that testing is ... important. How do you test your React Native apps? In this presentation I will show how to run full integrations tests using Cypress while the RN app is running in the browser. This method can cover most of the application's code and be effective at finding logical errors and mistakes when calling the server APIs.",
          people: [people.gleb],
          urls: {
            web: "https://slides.com/bahmutov/testing-react-native",
            video: "https://www.youtube.com/watch?v=PIxaFbMBez0",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "Haptics, Sounds and Micro Animations",
          description:
            "Quick tips on how to utilise the native mobile platform to help provide your users an upgraded experience with sounds, vibrations and animations. Small changes can have a huge difference UX-wise.",
          people: [people.kadiKraman],
          urls: {
            demo: "https://snack.expo.dev/@kadikraman/haptics-and-micro-animations",
            video: "https://www.youtube.com/watch?v=FLhvw0ntzBM",
          },
        },
      ],
      urls: {
        video: "https://www.youtube.com/watch?v=Qf3g2kkTgrk",
      },
    },
    {
      begin: "10:00",
      end: "11:30",
      title: "Scaling",
      mc: people.saraVieira,
      sessions: [
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title: "Design Systems of a Down: Steal this Guide!",
          description: `Remember Atomic Design? It's been a while since we started talking about Design Systems. They're supposed to solve our interfaces inconsistencies issues, as a single source of truth. But do you know well how to build and use them, from a developer perspective?

Here's your ultimate guide to Design Systems, for Devs! From the fundamentals of Design Tokens definition, to how to build advanced versatile layouts. You'll learn all best practices, tips & tricks, components splicing strategies, from this comprehensive step-by-step handbook talk.

Never be lost again in front of a creating Design System from scratch!`,
          people: [people.m4Dz],
          urls: {
            video: "https://www.youtube.com/watch?v=YRZN5a2py00",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.INTERMEDIATE,
          title:
            "How to structure, style and document your components for maximum effectiveness",
          description: `I'll present a practical approach to styling react components, a folder structure and documentation structure that's very easy to follow (for onboarding members) + other insights I got while using this approach.`,
          people: [people.dragos],
          urls: {
            video: "https://www.youtube.com/watch?v=3ZBl49_ZM0s",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.BEGINNER,
          title: "React on Steroids with Nx!",
          description: `We often talk and hear about scaling in production, but we rarely talk about scaling development. Aspects like structuring and organizing your codebase, code sharing across teams, speeding up build and test runs can play a huge role in terms of productivity for a developer team. That impacts small teams but gets even more evident as your team grows and especially for large enterprises.

In this short talk I'm going to demo Nx and what it can do for you to help you modularize your React code, generate code for you, help you build faster and much more. Tune in to learn about the build framework you've been missing in your tool belt!`,
          people: [people.juri],
          urls: {
            video: "https://www.youtube.com/watch?v=IyzsQwAN51Y",
            web: "https://docs.google.com/presentation/d/1FBOhMSUJ1fBQcgAlr8L7NsYbni35PxZ0xBm0NEJcv8Y/edit?usp=sharing",
          },
        },
      ],
      urls: {
        video: "https://www.youtube.com/watch?v=cSfpwlQthH4",
      },
    },
    {
      begin: "12:00",
      end: "13:30",
      title: "Techniques II",
      mc: people.saraVieira,
      sessions: [
        {
          type: SessionType.TALK,
          level: Level.BEGINNER,
          title:
            "Video streaming on the web: Live and recorded video streaming",
          description: `In this presentation, we'll present how video streaming works.  We'll look at the anatomy of video streaming, and how video streaming is beneficial for delivering your content over just adding mp4 files.

We'll demonstrate live streaming **from** the browser, and then how to integrate that live stream into your webpage.`,
          people: [people.doug],
          urls: {
            video: "https://www.youtube.com/watch?v=plf1aocFmZU",
            web: "https://docs.google.com/presentation/d/1DnaY-h81C-nhFqrzQQg9YXaoQUMnLk_c7L8kpBiq1Wk/edit?usp=sharing",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.BEGINNER,
          title: "On the Road to the World's Best Branding",
          description:
            "Branding. What's it all about? Is it good, or is it wack? Come found out as we take a closer how we do event branding and theming at Brella. We'll go through branding and color palette generation in general, look at the implementation in our end, and see what the future holds.",
          people: [people.niklas],
          urls: {
            video: "https://www.youtube.com/watch?v=iFbDeUYyA_I",
            web: "https://docs.google.com/presentation/d/1Qs-QZRVladLp06MqJ9HeH_Qak2Z76yAZIrk3EEm6qJ4/edit?usp=sharing",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.BEGINNER,
          title: "You thought your React application is secure? Think again",
          description:
            "Modern frontend frameworks like React are well thought-of in their application security design and that’s great. However, there is still plenty of room for developers to make mistakes and use insecure APIs, vulnerable components, or generally do the wrong thing that turns user input into a Cross-site Scripting vulnerability (XSS). Let me show you how React applications get hacked in the real-world.",
          people: [people.liran],
          urls: {
            slides:
              "https://drive.google.com/file/d/18xgG8wEA7vTGgVzWUkXXHNpbeLVtQo5v/view?usp=sharing",
            video: "https://www.youtube.com/watch?v=9YC0tFBxKsA",
          },
        },
      ],
      urls: {
        video: "https://www.youtube.com/watch?v=QFZacufAgK0",
      },
    },
    {
      begin: "15:00",
      end: "17:00",
      title: "The future of work",
      mc: people.juhoVepsalainen,
      sessions: [
        {
          type: SessionType.TALK,
          level: Level.BEGINNER,
          title: "The future of software development in Finland",
          description:
            "Finland's the happy nation and in need of very many newcomers to the country especially in the tech field. What's realistic to expect in the future Finnish job market as a developer? Where do we expect the developer jobs to progress in the future?",
          people: [people.saku],
          urls: {
            video: "https://www.youtube.com/watch?v=mj8maOaf8AY",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.BEGINNER,
          title: "Becoming a React developer - A hiring manager’s perspective",
          description:
            "The landscape of popular JS frameworks and libraries has become increasingly polarised. With React standing out an increasing number of companies are struggling to find skilled developers. People experienced in React are in a great position. Nevertheless, what if my CV lacks the right keyword? What if my Angular experience looks more like a burden? Can I apply for a front-end developer position in a React project?",
          people: [people.mika],
          urls: {
            video: "https://www.youtube.com/watch?v=QB4r9qFsUlo",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.BEGINNER,
          title: "The future of work is enjoyable",
          description: `We’re generally afraid of the future. Not only is it unknown, but there’s quite a lot of reasons to believe we’re practically doomed. And it doesn’t help that we’re negatively wired by nature, programmed to look out for risks more than search for rewards. Developers especially so.

So with this speech, I want to paint another picture for you. I genuinely believe, the future of work is enjoyable and definitely worth pursuing. And you can have that. Right after this speech.`,
          people: [people.elisa],
          urls: {
            video: "https://www.youtube.com/watch?v=75orGgAl-F4",
          },
        },
        {
          type: SessionType.TALK,
          level: Level.BEGINNER,
          title: "Closing",
          description:
            "The conference has reached its end. What did we learn and where do we go from here (2022)?",
          people: [people.juhoVepsalainen],
        },
      ],
      urls: {
        video: "https://www.youtube.com/watch?v=untSAGaDREg",
      },
    },
  ],
};

export default [monday, tuesday, wednesday, thursday, friday];
