import styled from "@emotion/styled";
import { Color } from "csstype";
import hexToRgba from "hex-to-rgba";
import get from "lodash/get";
import desaturate from "polished/lib/color/desaturate";
import * as React from "react";
import { Interval } from "../../schema/Interval";
import { Theme } from "../../schema/Theme";
import connect from "../components/connect";
import Schedule from "../components/Schedule";
import Sponsors from "../components/Sponsors";
import { dayToFinnishLocale } from "../date-utils";
import scheduleQuery from "../queries/scheduleQuery";
import sponsorQuery from "../queries/sponsorQuery";
interface SchedulePageContainerProps {
  id: string;
  primaryColor: Color;
  secondaryColor: Color;
  texture: string;
}

const ScheduleTemplateContainer = styled.article`
  display: grid;
  background-image: ${({
    primaryColor,
    secondaryColor,
    texture,
  }: SchedulePageContainerProps) => `linear-gradient(
      ${primaryColor},
      ${desaturate(0.2, hexToRgba(secondaryColor, 0.79))}
    ),
    url("${texture}")`};
  background-size: cover;
  position: relative;
  padding: 0;
  width: 29.7cm;
  height: 21cm;
  overflow: hidden;
` as React.FC<SchedulePageContainerProps>;

const ScheduleTemplateLogo = styled.img`
  position: relative;
  margin: 0cm 0.9cm 0cm;
  width: 5cm;
` as React.FC<{ src: string }>;

const ScheduleHeaderContainer = styled.section``;

const ScheduleTemplateTitle = styled.h1`
  color: white;
  position: absolute;
  right: 1.2cm;
  top: 0cm;
  font-size: 420%;
  margin-top: 0.25cm;
` as React.FC<{}>;

const ScheduleContentContainer = styled.div`
  display: grid;
  align-items: center;
  justify-items: center;
  position: relative;
  margin: 0;
  margin-top: -0.2cm;
  padding: 0.25cm 0 0.25cm 0.25cm;
  opacity: 0.9;
  background-color: white;
  height: 13.4cm;
  clip-path: polygon(0 0, 100% 1cm, 100% 100%, 0 calc(100% - 1cm));
  z-index: 1;
` as React.FC<{}>;

const PlaceholderContainer = styled.h1`
  font-size: 1000%;
` as React.FC<{}>;

interface ScheduleTemplateProps {
  intervals?: Interval[];
  theme: Theme;
  day: string;
  conferenceId: string;
  id: string;
  placeholder: string;
}

const ScheduleFooterContainer = styled.section`
  margin-top: -0.3cm;
`;

const sponsorRules = {
  gold: {
    "max-height": "2cm",
    "max-width": "3cm",
    margin: "0 0.5cm 0.5cm 0.5cm",
    display: "block",
  },
  silver: {
    "max-height": "1.25cm",
    "max-width": "1.75cm",
    margin: "0.5cm",
  },
  bronze: {
    "max-height": "1.25cm",
    "max-width": "1.75cm",
    margin: "0.25cm 0.5cm 0cm 0.5cm",
  },
};

const ConnectedSponsors = connect(
  "/graphql",
  sponsorQuery,
  ({ conferenceId }) => ({ conferenceId })
)(({ conference }) => <Sponsors {...conference} rules={sponsorRules} />);

function ScheduleTemplate({
  intervals,
  theme,
  day,
  conferenceId,
  id,
  placeholder,
}: ScheduleTemplateProps) {
  return (
    <ScheduleTemplateContainer
      id={id}
      primaryColor={theme.colors.primary}
      secondaryColor={theme.colors.secondary}
      texture={theme.textures[0].url}
    >
      <ScheduleHeaderContainer>
        <ScheduleTemplateLogo src={theme.logos.white.withText.url} />
        <ScheduleTemplateTitle>{day ? day : ""}</ScheduleTemplateTitle>
      </ScheduleHeaderContainer>
      <ScheduleContentContainer>
        {placeholder ? (
          <PlaceholderContainer>{placeholder}</PlaceholderContainer>
        ) : (
          intervals && <Schedule theme={theme} intervals={intervals} />
        )}
      </ScheduleContentContainer>
      <ScheduleFooterContainer>
        <ConnectedSponsors conferenceId={conferenceId} />
      </ScheduleFooterContainer>
    </ScheduleTemplateContainer>
  );
}

const ConnectedScheduleTemplate = connect(
  "/graphql",
  scheduleQuery,
  {},
  ({ conferenceId, day }) => ({ conferenceId, day })
)(({ schedule, theme, conferenceId, id, placeholder }) => (
  <ScheduleTemplate
    id={id}
    placeholder={placeholder}
    theme={theme}
    day={schedule && dayToFinnishLocale(schedule.day)}
    conferenceId={conferenceId}
    intervals={get(schedule, "intervals")}
  />
));

ConnectedScheduleTemplate.filename = "schedule";

// TODO: Better use enums here
ConnectedScheduleTemplate.variables = [
  {
    id: "placeholder",
    validation: { type: String, default: "" },
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
  {
    id: "day",
    query: `query DayQuery($conferenceId: ID!) {
  conference(id: $conferenceId) {
    schedules {
      day
    }
  }
}`,
    mapToCollection({ conference }) {
      return get(conference, "schedules", []);
    },
    mapToOption({ day }) {
      return {
        value: day,
        label: day,
      };
    },
  },
];

export default ConnectedScheduleTemplate;
