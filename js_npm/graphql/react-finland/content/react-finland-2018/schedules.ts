import { Schedule } from "../../server/schema/Schedule";
import { SessionType } from "../../server/schema/Session";
import * as talks from "./talks";
import * as workshops from "./workshops";

const workshopDay: Schedule = {
  day: "2018-04-24",
  description: "Workshop day",
  intervals: [
    {
      begin: "09:00",
      end: "13:00",
      sessions: [
        workshops.andreyAndArtemWorkshop,
        workshops.davidKhourshidWorkshop,
        workshops.gantLabordeWorkshop,
        workshops.juhoVepsalainenWorkshop,
        workshops.patrickAndNikWorkshop,
        workshops.saraVieiraWorkshop,
        workshops.siaKaramalegosWorkshop,
      ],
    },
    {
      begin: "14:00",
      end: "18:00",
      sessions: [
        workshops.andreyAndArtemWorkshop,
        workshops.davidKhourshidWorkshop,
        workshops.gantLabordeWorkshop,
        workshops.juhoVepsalainenWorkshop,
        workshops.patrickAndNikWorkshop,
        workshops.rotemMizrachiMeidanWorkshop,
        workshops.siaKaramalegosWorkshop,
        workshops.svenSauleauWorkshop,
      ],
    },
    {
      begin: "09:00",
      end: "18:00",
      sessions: [workshops.michelWeststrateWorkshop],
    },
  ],
};

const presentationDay1: Schedule = {
  day: "2018-04-25",
  description: "Presentation day",
  intervals: [
    {
      begin: "08:00",
      end: "09:00",
      sessions: [
        {
          type: SessionType.BREAKFAST,
          title: "Registration, Finnish breakfast.",
          description: "Trust me, it's the best.",
        },
      ],
    },
    {
      begin: "09:00",
      end: "09:15",
      sessions: [
        {
          type: SessionType.ORGANIZATIONAL,
          title: "Opening ceremonies",
        },
      ],
    },
    {
      begin: "09:15",
      end: "09:45",
      sessions: [talks.janiEvakallio],
    },
    {
      begin: "10:00",
      end: "10:45",
      sessions: [talks.christianAlfoni],
    },
    {
      begin: "11:00",
      end: "11:10",
      sessions: [talks.michelWeststrate],
    },
    {
      begin: "11:10",
      end: "11:20",
      sessions: [talks.patrickHund],
    },
    {
      begin: "11:20",
      end: "11:30",
      sessions: [talks.eemeliAro],
    },
    {
      begin: "11:30",
      end: "12:30",
      sessions: [
        {
          type: SessionType.LUNCH,
          title: "Lunch",
          description: "It's functional. You are **not** supposed to like it.",
        },
      ],
    },
    {
      begin: "12:30",
      end: "13:15",
      sessions: [talks.kasiaJastrzebska],
    },
    {
      begin: "13:30",
      end: "14:15",
      sessions: [talks.siaKaramalegos],
    },
    {
      begin: "14:15",
      end: "14:45",
      sessions: [
        {
          type: SessionType.COFFEE_BREAK,
          title: "Coffee break",
          description:
            "We don't mind if you drink tea, though. Water is available as well.",
        },
      ],
    },
    {
      begin: "14:45",
      end: "15:30",
      sessions: [talks.saraVieira],
    },
    {
      begin: "15:45",
      end: "16:15",
      sessions: [talks.rotemMizrachiMeidan],
    },
    {
      begin: "16:15",
      end: "16:45",
      sessions: [
        {
          type: SessionType.COFFEE_BREAK,
          title: "Coffee break",
          description:
            "Other beverages are available too although black coffee is our favorite.",
        },
      ],
    },
    {
      begin: "16:45",
      end: "17:00",
      sessions: [talks.andreyOkonetchnikov],
    },
    {
      begin: "17:00",
      end: "17:15",
      sessions: [talks.svenSauleau],
    },
    {
      begin: "17:15",
      end: "17:30",
      sessions: [talks.eemeliAro2],
    },
    {
      begin: "17:30",
      end: "18:00",
      sessions: [
        {
          type: SessionType.PANEL,
          title: "Panel",
          description: "Panel discussion to end the day",
        },
      ],
    },
  ],
};

const presentationDay2: Schedule = {
  day: "2018-04-26",
  description: "Presentation day",
  intervals: [
    {
      begin: "08:00",
      end: "09:00",
      sessions: [
        {
          type: SessionType.BREAKFAST,
          title: "Registration, Finnish breakfast.",
          description: "Trust me, it's the best still.",
        },
      ],
    },
    {
      begin: "09:00",
      end: "09:15",
      sessions: [
        {
          type: SessionType.ORGANIZATIONAL,
          title: "Opening ceremonies",
        },
      ],
    },
    {
      begin: "09:15",
      end: "09:45",
      sessions: [talks.kenWheeler],
    },
    {
      begin: "09:45",
      end: "10:00",
      sessions: [talks.juhoVepsalainen],
    },
    {
      begin: "10:15",
      end: "10:45",
      sessions: [talks.nikGraf],
    },
    {
      begin: "11:00",
      end: "11:30",
      sessions: [talks.patrickStapfer],
    },
    {
      begin: "11:30",
      end: "12:30",
      sessions: [
        {
          type: SessionType.LUNCH,
          title: "Lunch",
          description: "You might like it better this time",
        },
      ],
    },
    {
      begin: "12:30",
      end: "13:15",
      sessions: [talks.davidKhourshid],
    },
    {
      begin: "13:30",
      end: "14:15",
      sessions: [talks.shayKeinan],
    },
    {
      begin: "14:15",
      end: "14:45",
      sessions: [
        {
          type: SessionType.COFFEE_BREAK,
          title: "Coffee break",
          description:
            "We don't mind if you drink tea, though. Water is available as well.",
        },
      ],
    },
    {
      begin: "14:45",
      end: "15:15",
      sessions: [talks.michalChudziak],
    },
    {
      begin: "15:15",
      end: "15:30",
      sessions: [talks.toniRistola],
    },
    {
      begin: "15:45",
      end: "16:30",
      sessions: [talks.gantLaborde],
    },
    {
      begin: "16:45",
      end: "17:00",
      sessions: [talks.varyaStepanova],
    },
    {
      begin: "17:00",
      end: "17:30",
      sessions: [
        {
          type: SessionType.PANEL,
          title: "Panel",
          description: "Panel discussion to end the conference",
        },
      ],
    },
    {
      begin: "18:30",
      end: "18:45",
      sessions: [
        {
          type: SessionType.ORGANIZATIONAL,
          title: "Buses leave to after party (Sea Life, Tivolikuja 10)",
        },
      ],
    },
  ],
};

export default [workshopDay, presentationDay1, presentationDay2];
