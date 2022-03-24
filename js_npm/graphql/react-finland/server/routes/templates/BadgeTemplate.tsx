import styled from "@emotion/styled";
import { Color, WidthProperty } from "csstype";
import chunk from "lodash/chunk";
import flatten from "lodash/flatten";
import get from "lodash/get";
import map from "lodash/map";
import * as React from "react";
import { Contact, ContactType } from "../../schema/Contact";
import { Theme } from "../../schema/Theme";
import Badge from "../components/Badge";
import connect from "../components/connect";

const BadgeTemplateContainer = styled.section``;

interface BadgeTemplateProps {
  attendees: Contact[];
  theme: Theme;
  id: string;
  showOnlyTemplates: boolean;
  emptyAttendees: number;
  emptyOrganizers: number;
  emptyPress: number;
  emptySpeakers: number;
  emptySponsors: number;
  // TODO: Likely a better structure
  /*
  emptyAmounts: {
    [AttendeeType.ATTENDEE]: number;
    [AttendeeType.ORGANIZER]: number;
    [AttendeeType.SPEAKER]: number;
    [AttendeeType.SPONSOR]: number;
  };
  */
}

const defaultAttendees: Contact[] = [
  {
    type: [ContactType.ATTENDEE],
    name: "John Longsurname-Anotherlongone",
    social: {
      twitter: "johndoe",
    },
    company: "John Doe Enterprises",
    about: "",
    image: { url: "" },
    location: {},
  },
  {
    type: [ContactType.ORGANIZER],
    name: "Jane Doe",
    social: {
      twitter: "johndoe",
    },
    company: "John Doe Enterprises",
    about: "",
    image: { url: "" },
    location: {},
  },
  {
    type: [ContactType.PRESS],
    name: "Janette Doe",
    social: {
      twitter: "janettedoe",
    },
    company: "News Corp",
    about: "",
    image: { url: "" },
    location: {},
  },
  {
    type: [ContactType.SPEAKER],
    name: "John Doedoedoedoedoedoedoe",
    social: {
      twitter: "johndoe",
    },
    company: "John Doe Enterprises",
    about: "",
    image: { url: "" },
    location: {},
  },
  {
    type: [ContactType.SPONSOR],
    name: "John-Jack-Jim-Joe-Joel-Jeff Doe",
    social: {
      twitter: "johndoe",
    },
    company: "John & Son Doe Enterprises & Co.",
    about: "",
    image: { url: "" },
    location: {},
  },
];

function BadgeTemplate({
  id,
  theme,
  attendees = defaultAttendees,
  showOnlyTemplates,
  emptyAttendees = 0,
  emptyOrganizers = 0,
  emptyPress = 0,
  emptySpeakers = 0,
  emptySponsors = 0,
}: BadgeTemplateProps) {
  const badgesPerPage = 4;
  let pages;

  if (showOnlyTemplates) {
    pages = chunk(
      getBadgeData(
        [
          { type: [ContactType.ATTENDEE] },
          { type: [ContactType.ORGANIZER] },
          { type: [ContactType.PRESS] },
          { type: [ContactType.SPEAKER] },
          { type: [ContactType.SPONSOR] },
        ],
        {
          [ContactType.ATTENDEE]: 0,
          [ContactType.ORGANIZER]: 0,
          [ContactType.PRESS]: 0,
          [ContactType.SPEAKER]: 0,
          [ContactType.SPONSOR]: 0,
        }
      ),
      badgesPerPage
    );
  } else {
    pages = chunk(
      fillWithEmpties(
        getBadgeData(attendees, {
          [ContactType.ATTENDEE]: emptyAttendees,
          [ContactType.ORGANIZER]: emptyOrganizers,
          [ContactType.PRESS]: emptyPress,
          [ContactType.SPEAKER]: emptySpeakers,
          [ContactType.SPONSOR]: emptySponsors,
        }),
        badgesPerPage
      ),
      badgesPerPage
    );
  }

  return (
    <BadgeTemplateContainer id={id}>
      {map(pages, (tickets, i) => (
        <Page
          key={i}
          defaultColor={theme.colors.primary}
          logo={theme.logos.white.withText.url}
          texture={theme.textures[0].url}
          tickets={tickets}
          showBackside={!showOnlyTemplates}
        />
      ))}
    </BadgeTemplateContainer>
  );
}

const ConnectedBadgeTemplate = connect(
  "/graphql",
  `query AttendeeQuery($conferenceId: ID!) {
  conference(id: $conferenceId) {
    attendees {
      name
      company
      type
      social {
        twitter
      }
    }
  }
}`,
  {},
  ({ conferenceId }) => ({ conferenceId })
)(
  ({
    conference,
    id,
    theme,
    showOnlyTemplates,
    emptyAttendees,
    emptyOrganizers,
    emptyPress,
    emptySpeakers,
    emptySponsors,
  }) => (
    <BadgeTemplate
      attendees={get(conference, "attendees")}
      id={id}
      theme={theme}
      showOnlyTemplates={showOnlyTemplates}
      emptyAttendees={emptyAttendees}
      emptyOrganizers={emptyOrganizers}
      emptyPress={emptyPress}
      emptySpeakers={emptySpeakers}
      emptySponsors={emptySponsors}
    />
  )
);

ConnectedBadgeTemplate.filename = "badge";

ConnectedBadgeTemplate.variables = [
  {
    id: "showOnlyTemplates",
    validation: {
      type: Boolean,
      default: true,
    },
  },
  {
    id: "conferenceId",
    query: `query ConferenceIdQuery {  
  conferences {
    id
    name
  }
}`,
    mapToCollection({ conferences }) {
      return conferences;
    },
    mapToOption({ id, name }) {
      return {
        value: id,
        label: name,
      };
    },
  },
  // TODO
  /*{
    id: 'attendees',
    validation: {
      type: Array,
      of: Attendee,
      default: defaultAttendees
    }
  }*/
  // TODO: Figure out how to model this case.
  // Ideally this would be abstracted further so it's
  // not worth solving yet.
  /*{
    id: "emptyAmounts",
    validation: {
      type: Object,
      default: {
        attendees: 0,
        organizers: 0,
        speakers: 0,
        sponsors: 0,
      },
    },
  },*/
  {
    id: "emptyAttendees",
    validation: {
      type: Number,
      default: 0,
    },
  },
  {
    id: "emptyOrganizers",
    validation: {
      type: Number,
      default: 0,
    },
  },
  {
    id: "emptyPress",
    validation: {
      type: Number,
      default: 0,
    },
  },
  {
    id: "emptySpeakers",
    validation: {
      type: Number,
      default: 0,
    },
  },
  {
    id: "emptySponsors",
    validation: {
      type: Number,
      default: 0,
    },
  },
];

interface PageProps {
  defaultColor: Color;
  logo: string;
  texture: string;
  tickets: any[]; // TODO
  showBackside: boolean;
}

interface PageSheetProps {
  width: WidthProperty<string>;
}

// TODO: Is the Safari fix needed anymore?
const PageSheet = styled.section`
  text-align: center;
  width: ${({ width }) => width};
  display: flex;
  flex-wrap: wrap;

  @media print {
    height: 11.5in !important; /* Adjust for Safari print mode */
  }
` as React.FC<PageSheetProps>;

const PageBadgeContainer = styled.div`
  display: inline-block;
`;

function Page({
  defaultColor,
  logo,
  texture,
  tickets = [],
  showBackside,
}: PageProps) {
  const pairs = chunk(tickets, 2);
  const reverse = flatten(pairs.map(pair => [pair[1], pair[0]]));
  const width = "105mm"; // A6
  const height = "148mm"; // A6

  // TODO: Figure out how to deal withpage width properly
  // TODO: Implement separate layouts for front/back. Now we just
  // use the same.
  return (
    <>
      <PageSheet width="210mm">
        {tickets.map((attendee: Contact, i) => (
          <PageBadgeContainer key={`front-${i}`}>
            <Badge
              attendee={attendee}
              defaultColor={defaultColor}
              logo={logo}
              texture={texture}
              width={width}
              height={height}
            />
          </PageBadgeContainer>
        ))}
      </PageSheet>
      {showBackside && (
        <PageSheet width="210mm">
          {reverse.map((attendee, i) => (
            <PageBadgeContainer key={`back-${i}`}>
              <Badge
                attendee={attendee}
                defaultColor={defaultColor}
                logo={logo}
                texture={texture}
                width={width}
                height={height}
              />
            </PageBadgeContainer>
          ))}
        </PageSheet>
      )}
    </>
  );
}

type BadgeTypes =
  | ContactType.ATTENDEE
  | ContactType.ORGANIZER
  | ContactType.PRESS
  | ContactType.SPEAKER
  | ContactType.SPONSOR;

function getBadgeData(
  tickets,
  emptyAmounts: { [key in BadgeTypes]: number } = {
    [ContactType.ATTENDEE]: 0,
    [ContactType.ORGANIZER]: 0,
    [ContactType.PRESS]: 0,
    [ContactType.SPEAKER]: 0,
    [ContactType.SPONSOR]: 0,
  }
) {
  const ret = tickets.concat(
    flatten(
      map(emptyAmounts, (amount, type) =>
        //  TODO: See if it's possible to eliminate "as" from here
        Array(amount).fill(getEmptyData(type as ContactType))
      )
    )
  );

  return ret;
}

function fillWithEmpties(badges, badgesPerPage, type = ContactType.ATTENDEE) {
  return badges.concat(
    Array(badges.length % badgesPerPage).fill(getEmptyData(type))
  );
}

function getEmptyData(type: ContactType): Contact {
  return {
    type: [type],
    name: "",
    company: "",
    about: "",
    image: { url: "" },
    social: {
      twitter: "",
    },
    location: {},
  };
}

// TODO: Connect to ticket API

export default ConnectedBadgeTemplate;
