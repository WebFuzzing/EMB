import { Schedule } from "../../server/schema/Schedule";
import { SessionType } from "../../server/schema/Session";
import * as locations from "../locations";
// import * as people from "../people";
import * as talks from "./talks";

const presentationDay: Schedule = {
  day: "2020-06-01",
  description: "Conference day",
  location: locations.amanda,
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
          people: [],
          title: "Opening",
          description: "Opening the day with Sec Ret, our MC of the day",
          type: SessionType.ORGANIZATIONAL,
        },
      ],
    },
    {
      begin: "09:15",
      end: "10:45",
      title: "Secret",
      sessions: [talks.georgeMandis, talks.kristaJantti],
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
      title: "Secret",
      sessions: [],
    },
    {
      begin: "12:15",
      end: "13:15",
      sessions: [
        {
          type: SessionType.LUNCH,
          title: "Lunch",
          description:
            "Hopefully it's as disappointing as the one you had at previous React Finland",
        },
      ],
    },
    {
      begin: "13:15",
      end: "15:00",
      title: "Let's code an app!",
      sessions: [
        talks.nataliaTepluhina,
        talks.patrickStapfer,
        talks.kenWheeler,
      ],
    },
    {
      begin: "15:00",
      end: "15:30",
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
      begin: "15:30",
      end: "16:30",
      title: "Secret",
      sessions: [talks.teroParviainen, talks.bodil],
    },
    {
      begin: "16:30",
      end: "17:00",
      sessions: [
        {
          type: SessionType.ORGANIZATIONAL,
          title: "Closing ceremonies",
        },
      ],
    },
    {
      begin: "18:00",
      end: "22:00",
      sessions: [
        {
          type: SessionType.PARTY,
          title: "Afterparty",
        },
      ],
    },
  ],
};

export default [presentationDay];
