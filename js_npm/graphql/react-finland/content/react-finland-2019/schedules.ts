import { Schedule } from "../../server/schema/Schedule";
import { SessionType } from "../../server/schema/Session";
import * as locations from "../locations";
import * as people from "../people";
import * as talks from "./talks";
import * as allWorkshops from "./workshops";

const workshops = Object.values(allWorkshops);

const linnanmaki = {
  type: SessionType.PARTY,
  title: "Visiting Linnanmäki Amusement Park ☀️",
  description: `[Linnanmäki](https://www.linnanmaki.fi/en) is the amusement park of Helsinki. Expect to be amused on its opening weekend!
  `,
  location: {
    city: "Helsinki",
    country: {
      code: "FI",
      name: "Finland",
    },
    address: "Tivolikuja 1",
  },
};

const suomenlinna = {
  type: SessionType.PARTY,
  title: "Visiting Suomenlinna Castle ☀️",
  description: `[Suomenlinna](https://www.suomenlinna.fi/en/) is one of the most significant castles in Finland. The world heritage site has a lot to explore. You need to take a ferry to reach it.
  `,
  location: {
    city: "Helsinki",
    country: {
      code: "FI",
      name: "Finland",
    },
    address: "Suomenlinna",
  },
};

const oodi = {
  type: SessionType.PARTY,
  title: "Visiting Oodi - The New Central Library 🌧",
  description: `[Oodi](https://www.oodihelsinki.fi/en/) is the brand new central library of Helsinki. Although visiting a library might sound boring, the building is a little more than that.
  `,
  location: {
    city: "Helsinki",
    country: {
      code: "FI",
      name: "Finland",
    },
    address: "Töölönlahdenkatu 4",
  },
};

const swimming = {
  type: SessionType.PARTY,
  title: "Swimming at Yrjönkatu swimming hall 🌧",
  description: `[Yrjönkatu swimming hall](https://palvelukartta.hel.fi/unit/41102) is Finland's first and oldest public swimming hall inaugurated in 1928. According to the BBC, it is one of the most beautiful swimming halls in the world.

Swimming is limited to women/men only per day (women: Mon, Wed, Fri, Sun, men: Tue, Thu, Sat) as clothing is optional. You can also find multiple saunas. Depending on the services you want, expect to spend between 7 and 30 euros.

The swimming hall is open daily (closed 22.04 due to Easter).
  `,
  location: {
    city: "Helsinki",
    country: {
      code: "FI",
      name: "Finland",
    },
    address: "Yrjönkatu 21 b",
  },
};

const iceCreamCarnival = {
  type: SessionType.PARTY,
  title: "The Carnival of Ice Cream and Chocolate 🌧",
  description: `Finland's first [carnival of ice cream and chocolate](https://www.jaatelojasuklaakarnevaali.fi/en/home/) takes place between 26th and 28th of April. If you like either or both, it's a great chance to pick up some local flavour (liquorice ice cream anyone?).`,
  location: {
    city: "Helsinki",
    country: {
      code: "FI",
      name: "Finland",
    },
    address: "Tallberginkatu 1 C 176 (3rd floor)",
  },
};

const preConferenceDay: Schedule = {
  day: "2019-04-23",
  description: "Pre-conference",
  intervals: [
    {
      begin: "08:00",
      end: "22:00",
      sessions: [oodi],
    },
    {
      begin: "10:00",
      end: "16:00",
      sessions: [suomenlinna],
    },
    {
      begin: "14:00",
      end: "20:00",
      sessions: [swimming],
    },
    {
      begin: "18:00",
      end: "21:00",
      sessions: [
        {
          type: SessionType.PARTY,
          title: "React Helsinki meetup",
          description: `Meet local developers in React Helsinki meetup.

> [Please register separately if you are coming](https://meetabit.com/events/react-helsinki-april-2019).`,
        },
      ],
    },
  ],
};

const workshopDay: Schedule = {
  day: "2019-04-24",
  description: "Workshop day",
  location: locations.paasitorni,
  intervals: [
    {
      begin: "08:00",
      end: "09:00",
      sessions: [
        {
          type: SessionType.ORGANIZATIONAL,
          title: "Registration and breakfast",
          description:
            "Pro tip: You can register at any time during the conference. There's light breakfast (pastry and coffee) available.",
        },
      ],
    },
    {
      begin: "09:00",
      end: "11:30",
      sessions: workshops,
    },
    {
      begin: "11:30",
      end: "12:30",
      sessions: [
        {
          type: SessionType.LUNCH,
          title: "Lunch",
        },
      ],
    },
    {
      begin: "12:30",
      end: "14:00",
      title: "Workshops continue",
      sessions: [],
    },
    {
      begin: "14:00",
      end: "14:20",
      sessions: [
        {
          type: SessionType.COFFEE_BREAK,
          title: "Break",
        },
      ],
    },
    {
      begin: "14:20",
      end: "15:40",
      title: "Workshops continue",
      sessions: [],
    },
    {
      begin: "15:40",
      end: "16:00",
      sessions: [
        {
          type: SessionType.COFFEE_BREAK,
          title: "Break",
        },
      ],
    },
    {
      begin: "16:00",
      end: "17:30",
      title: "Workshops continue",
      sessions: [],
    },
    {
      begin: "18:00",
      end: "23:00",
      sessions: [
        {
          title: "Sauna with Reaktor",
          description: `One of our sponsors is organizing an unofficial event where you can go to sauna and meet developers. The event also has food and drinks so you won't go hungry/thirsty and there will be transfer from the venue.

> [Please register separately if you are coming](https://www.reaktor.com/react-finland-afterparty/).`,
          type: SessionType.PARTY,
        },
      ],
    },
  ],
};

const presentationDay1: Schedule = {
  day: "2019-04-25",
  description: "First conference day",
  location: locations.paasitorni,
  intervals: [
    {
      begin: "08:00",
      end: "09:00",
      sessions: [
        {
          type: SessionType.ORGANIZATIONAL,
          title: "Registration and breakfast",
          description:
            "You can register later too. There's light breakfast (pastry and coffee) available.",
        },
      ],
    },
    {
      begin: "09:00",
      end: "09:15",
      sessions: [
        {
          people: [people.janiEvakallio],
          title: "Opening",
          description: "Opening the day with Jani Eväkallio, our MC of the day",
          type: SessionType.ORGANIZATIONAL,
        },
      ],
    },
    {
      begin: "09:15",
      end: "10:45",
      title: "State management",
      sessions: [
        talks.michelWeststrate,
        talks.lucaMatteis,
        talks.davidKhourshid,
        talks.farzadYz,
      ],
    },
    {
      begin: "10:45",
      end: "11:15",
      sessions: [
        {
          type: SessionType.COFFEE_BREAK,
          title: "Break",
          description:
            "Drink coffee, live long and prosper. Play foosball at the sponsors' lounge.",
        },
      ],
    },
    {
      begin: "11:15",
      end: "12:15",
      title: "Design Systems",
      sessions: [talks.andreyOkonetchnikov, talks.varyaStepanova],
    },
    {
      begin: "12:15",
      end: "13:15",
      sessions: [
        {
          type: SessionType.LUNCH,
          title: "Lunch",
          description:
            "Hopefully it's as disappointing as the one you had at GraphQL Finland",
        },
      ],
    },
    {
      begin: "13:15",
      end: "14:30",
      title: "Testing",
      sessions: [
        talks.juhaLinnanen,
        talks.maaretPyhajarvi,
        talks.manuelMatuzovic,
      ],
    },
    {
      begin: "14:30",
      end: "15:00",
      sessions: [
        {
          type: SessionType.COFFEE_BREAK,
          title: "Break",
          description:
            "Drink coffee. Again. Take a massage at the sponsors' lounge.",
        },
      ],
    },
    {
      begin: "15:00",
      end: "16:00",
      title: "Case Studies",
      sessions: [talks.kadiKraman, talks.glennReyes, talks.dustinSchau],
    },
    {
      begin: "16:00",
      end: "16:30",
      sessions: [
        {
          type: SessionType.COFFEE_BREAK,
          title: "Break",
          description:
            "Drink coffee if you still can. Enjoy the sofa at the sponsors' lounge.",
        },
      ],
    },
    {
      begin: "16:30",
      end: "17:15",
      title: "Visual",
      sessions: [talks.brunoLourenco, talks.nikGraf],
    },
    {
      begin: "17:15",
      end: "17:30",
      sessions: [
        {
          type: SessionType.ORGANIZATIONAL,
          title: "Closing ceremonies",
        },
      ],
    },
    {
      begin: "17:30",
      end: "18:00",
      sessions: [
        {
          type: SessionType.COFFEE_BREAK,
          title: "Break before smoosh",
        },
      ],
    },
    {
      begin: "18:00",
      end: "20:00",
      sessions: [
        {
          people: [people.janiEvakallio],
          title: "#SMOOSH",
          description: `After sold out shows in London and Berlin, we’re bringing the [#SMOOSH comedy show](https://smoosh.fun) to Helsinki!

Join us for an evening of standup, improv, and even a sing-along. This show will be unforgettable, no matter how much you try to forget it. 😉 

The event is held at the main venue!`,
          type: SessionType.PARTY,
        },
      ],
    },
  ],
};

const presentationDay2: Schedule = {
  day: "2019-04-26",
  description: "Second conference day",
  location: locations.paasitorni,
  intervals: [
    {
      begin: "08:00",
      end: "09:00",
      sessions: [
        {
          type: SessionType.ORGANIZATIONAL,
          title: "Registration and breakfast",
          description:
            "If you slept late, you can still register after this. There's light breakfast (pastry and coffee) available.",
        },
      ],
    },
    {
      begin: "09:00",
      end: "09:15",
      sessions: [
        {
          people: [people.saraVieira],
          title: "Opening",
          description: "Opening the day with Sara Vieira, our MC of the day",
          type: SessionType.ORGANIZATIONAL,
        },
      ],
    },
    {
      begin: "09:15",
      end: "10:45",
      title: "React Native",
      sessions: [talks.jamonHolmgren, talks.rickHanlon, talks.villeImmonen],
    },
    {
      begin: "10:45",
      end: "11:15",
      sessions: [
        {
          type: SessionType.COFFEE_BREAK,
          title: "Break",
          description:
            "Drink coffee and play games at the sponsors' lounge. Finnish coffee is darker than you think.",
        },
      ],
    },
    {
      begin: "11:15",
      end: "12:15",
      title: "Styling",
      sessions: [talks.artemSapegin, talks.artemZakharchenko],
    },
    {
      begin: "12:15",
      end: "13:15",
      sessions: [
        {
          type: SessionType.LUNCH,
          title: "Lunch",
          description: "Hopefully it's as disappointing as day before.",
        },
      ],
    },
    {
      begin: "13:15",
      end: "14:45",
      title: "Tooling",
      sessions: [
        talks.tejasKumar,
        talks.bohdanLiashenko,
        talks.tomasKonrady,
        talks.christofferNiska,
      ],
    },
    {
      begin: "14:45",
      end: "15:15",
      sessions: [
        {
          type: SessionType.COFFEE_BREAK,
          title: "Break",
          description:
            "Drink coffee. Again. Play more foosball at the sponsors' lounge.",
        },
      ],
    },
    {
      begin: "15:15",
      end: "16:00",
      title: "Performance",
      sessions: [talks.annaDoubkova, talks.svenSauleau],
    },
    {
      begin: "16:00",
      end: "16:30",
      sessions: [
        {
          type: SessionType.COFFEE_BREAK,
          title: "Break",
          description:
            "Drink coffee if you still can. Still time for a massage at the sponsors' lounge.",
        },
      ],
    },
    {
      begin: "16:30",
      end: "17:30",
      title: "Architecture",
      sessions: [talks.monicaLent, talks.carolynStransky],
    },
    {
      begin: "17:30",
      end: "18:00",
      sessions: [
        {
          type: SessionType.ORGANIZATIONAL,
          title: "Closing ceremonies",
        },
      ],
    },
    {
      begin: "18:00",
      end: "02:00",
      sessions: [
        {
          type: SessionType.ORGANIZATIONAL,
          title: "Afterparty",
        },
      ],
      location: locations.tiivistamo,
    },
  ],
};

const postConferenceDay1: Schedule = {
  day: "2019-04-27",
  description: "Post-conference",
  intervals: [
    {
      begin: "10:00",
      end: "16:00",
      sessions: [suomenlinna],
    },
    {
      begin: "10:00",
      end: "14:00",
      sessions: [iceCreamCarnival],
    },
    {
      begin: "10:00",
      end: "20:00",
      sessions: [oodi],
    },
    {
      begin: "13:00",
      end: "21:00",
      sessions: [linnanmaki],
    },
    {
      begin: "14:00",
      end: "20:00",
      sessions: [swimming],
    },
    {
      begin: "14:30",
      end: "18:30",
      sessions: [iceCreamCarnival],
    },
    {
      begin: "19:00",
      end: "23:00",
      sessions: [
        {
          type: SessionType.PARTY,
          title: "Unofficial bar meet",
          description: `Meet local developers in a local bar.

More info to come.`,
        },
      ],
    },
  ],
};

const postConferenceDay2: Schedule = {
  day: "2019-04-28",
  description: "Post-conference",
  intervals: [
    {
      begin: "10:00",
      end: "16:00",
      sessions: [suomenlinna],
    },
    {
      begin: "10:00",
      end: "14:00",
      sessions: [iceCreamCarnival],
    },
    {
      begin: "10:00",
      end: "20:00",
      sessions: [oodi],
    },
    {
      begin: "13:00",
      end: "21:00",
      sessions: [linnanmaki],
    },
    {
      begin: "14:00",
      end: "20:00",
      sessions: [swimming],
    },
    {
      begin: "14:30",
      end: "18:30",
      sessions: [iceCreamCarnival],
    },
  ],
};

export default [
  preConferenceDay,
  workshopDay,
  presentationDay1,
  presentationDay2,
  postConferenceDay1,
  postConferenceDay2,
];
