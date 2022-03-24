import styled from "@emotion/styled";
import * as CSS from "csstype";
import map from "lodash/map";
import * as React from "react";
// import emoji from "react-easy-emoji";
import { Interval } from "../../schema/Interval";
import SessionType from "../../schema/SessionType";
import { Theme } from "../../schema/Theme";

// TODO: Move control for margin/height to consumer
const ScheduleContainer = styled.dl`
  margin-top: 0.7cm;
  width: 100%;
  display: flex;
  flex-flow: column wrap;
  height: 12.5cm;
`;

const ScheduleContainerItem = styled.div`
  width: 50%;
  margin-bottom: 0.25cm;
  display: grid;
  grid-template-columns: 0.5fr 1.9fr;
  font-size: 83%;

  &:first-of-type {
    margin-top: -0.6cm;
  }
`;

interface ScheduleTitleProps {
  color: CSS.Color;
  type: SessionType;
}

const ScheduleTitle = styled.dt`
  display: inline;
  overflow: hidden;
  font-size: 150%;
  font-family: monospace;
  color: ${({ color, type }: ScheduleTitleProps) =>
    [
      SessionType.BREAKFAST,
      SessionType.COFFEE_BREAK,
      SessionType.PARTY,
      SessionType.LUNCH,
      SessionType.ORGANIZATIONAL,
    ].includes(type)
      ? color
      : "inherit"};
` as React.FC<ScheduleTitleProps>;

const ScheduleDefinition = styled.dd`
  display: inline;
  font-size: 99%;
  margin-left: 0cm;
  text-align: left;
  overflow: hidden;
  line-height: 1.45;
`;

interface ScheduleProps {
  theme: Theme;
  intervals: Interval[];
}

function Schedule({ theme, intervals }: ScheduleProps) {
  if (!intervals) {
    return null;
  }

  return (
    <ScheduleContainer>
      {map(intervals, ({ begin, end, sessions, title }, i) => (
        <ScheduleContainerItem key={`schedule-container-${i}`}>
          <ScheduleTitle
            key={`dt-${i}`}
            color={theme.colors.secondary}
            type={sessions[0] && sessions[0].type}
          >
            {begin}-{end}
          </ScheduleTitle>
          <ScheduleDefinition key={`dd-${i}`}>
            {sessions.length > 0 ? (
              sessions.map(({ title, type, people }, i) => (
                <SessionTitle
                  key={i}
                  title={title}
                  type={type}
                  people={people}
                />
              ))
            ) : (
              <SessionTitle title={title} />
            )}
          </ScheduleDefinition>
        </ScheduleContainerItem>
      ))}
    </ScheduleContainer>
  );
}

const SessionTitleContainer = styled.h3`
  margin-bottom: 0.15cm;
`;

interface SessionTitleProps {
  title?: string;
  type?: SessionType;
  people?: Array<{ name: string }>;
}

function SessionTitle({ title, type, people = [] }: SessionTitleProps) {
  return (
    <SessionTitleContainer>
      {title}{" "}
      {people &&
        Array.isArray(people) &&
        people.length > 0 &&
        `- ${people[0].name}`}{" "}
      {/*type && <ScheduleIcon type={type} /> TODO: Make toggleable */}
    </SessionTitleContainer>
  );
}

/*
function ScheduleIcon({ type }) {
  const scheduleTypes = {
    KEYNOTE: { icon: "🗝", title: "Keynote" },
    LIGHTNING_TALK: { icon: "⚡️", title: "Lightning talk" },
    TALK: { icon: "🎙", title: "Presentation" },
    PANEL: { icon: "🙋", title: "Panel" },
    WORKSHOP: { icon: "👩‍💻", title: "Workshop" },
    COFFEE_BREAK: { icon: "☕", title: "Coffee break" },
    BREAKFAST: { icon: "🥐", title: "Breakfast" },
    LUNCH: { icon: "🍕", title: "Lunch" },
    ORGANIZATIONAL: { icon: "💤", title: "Organizational" },
    PARTY: { icon: "🎉", title: "Party at Gofore office" },
  };

  return (
    <span title={scheduleTypes[type].title}>
      {emoji(scheduleTypes[type].icon)}
    </span>
  );
}
*/

export default Schedule;
