import styled from "@emotion/styled";
import { Color, FontFamilyProperty } from "csstype";
import hexToRgba from "hex-to-rgba";
import get from "lodash/get";
import map from "lodash/map";
import flatten from "lodash/flatten";
import * as React from "react";
import CopyToClipboard from "react-copy-to-clipboard";
import { Contact } from "../../schema/Contact";
import { Conference } from "../../schema/Conference";
import { Interval } from "../../schema/Interval";
import { Theme } from "../../schema/Theme";
import connect from "../components/connect";
import { dayToFinnishLocale } from "../date-utils";

const TweetTemplateContainer = styled.div``;
const TweetPageContainer = styled.div``;

interface TweetContainerProps {
  primaryColor: Color;
  secondaryColor: Color;
  texture: string;
}

const TweetContainer = styled.div`
  background-image: ${({
    primaryColor,
    secondaryColor,
    texture,
  }: TweetContainerProps) => `linear-gradient(
      ${primaryColor},
      ${hexToRgba(secondaryColor, 0.79)}
    ),
    url("${texture}")`};
  background-size: cover;
  position: relative;
  width: 880px;
  height: 440px;
  overflow: hidden;
  display: grid;
  grid-template-columns: 1fr 1fr;
  color: white;
` as React.FC<TweetContainerProps>;

const TweetTextContainer = styled.div`
  position: relative;
  margin-top: 2em;
  padding: 0.25em;
  max-width: 880px;
  background-color: #fff6c8;
`;
const TweetTextToCopy = styled.span`
  margin-right: 0.5em;
`;
const TweetCopyButton = styled.button`
  position: absolute;
  right: 0;
  top: 0;
  z-index: 1;
`;

const TweetRow = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  align-items: center;
`;

const TweetLogo = styled.img`
  max-width: 10em;
  margin-left: -0.5em;
`;

const TweetConferenceDays = styled.h3``;

const TweetText = styled.h2`
  text-align: center;
  padding-top: 0.5em;
  font-size: 300%;
`;

interface TweetDescriptionProps {
  fontFamily: FontFamilyProperty;
}

const TweetDescription = styled.h2`
  text-align: center;
  line-height: 1.5;
  width: 100%;
  font-size: 150%;
  font-weight: 200;
  font-family: ${({ fontFamily }: TweetDescriptionProps) => fontFamily};
` as React.FC<TweetDescriptionProps>;

interface SpeakerTweetTemplateProps {
  conference?: Conference;
  interval: Interval;
  theme: Theme;
  id: string;
}

function SpeakerTweetTemplate({
  conference,
  interval,
  theme,
  id,
}: SpeakerTweetTemplateProps) {
  if (!conference) {
    return null;
  }

  const days = getConferenceDays(conference);
  // TODO: Set up conference.hashtag (should not contain #)
  const tweetTextToCopy = `Learn more about the topic at #${conference.name
    .split(" ")
    .join("")} (${days})`;

  return (
    <TweetTemplateContainer>
      <TweetPageContainer id={id}>
        {React.createElement(SpeakerTweet, {
          logo: theme.logos.white.withText.url,
          days,
          interval,
          theme,
          conference,
        })}
      </TweetPageContainer>
      <TweetTextContainer>
        <TweetTextToCopy>{tweetTextToCopy}</TweetTextToCopy>
        <CopyToClipboard
          text={tweetTextToCopy}
          onCopy={() => alert("Copied to clipboard")}
        >
          <TweetCopyButton>&#x2398;</TweetCopyButton>
        </CopyToClipboard>
      </TweetTextContainer>
    </TweetTemplateContainer>
  );
}

function getConferenceDays(conference): string {
  const { schedules } = conference;
  const conferenceDays = map(schedules, ({ day }) => dayToFinnishLocale(day));
  const firstDay = conferenceDays[0];
  const lastDay = conferenceDays[conferenceDays.length - 1];

  return `${firstDay}-${lastDay}`;
}

interface TweetProps {
  days: string;
  interval: Interval;
  theme: Theme;
  conference: Conference;
}

const TweetInfoContainer = styled.div`
  padding: 3em;
  display: grid;
  justify-content: center;
`;

const TweetImageContainer = styled.div`
  background: white;
  padding: 3em;
  display: grid;
  grid-template-columns: 1fr 1fr;
`;

interface TweetImageProps {
  color?: Color;
  isCircle?: boolean;
  src: HTMLImageElement["src"];
  borderColor?: string;
}

const TweetImage = styled.img`
  width: 100%;
  box-sizing: border-box;
  border: 0.25em solid ${({ borderColor }: TweetImageProps) => borderColor};
  border-radius: 50%;
` as React.FC<TweetImageProps>;

const Centered = styled.div`
  position: absolute;
  margin: auto;
  width: 150px;
  left: 16%;
  top: 50%;
  margin-top: -75px;
` as React.FC;

function SpeakerTweet({ days, interval, theme }: TweetProps) {
  if (!interval) {
    return null;
  }

  const logo = theme.logos.white.withText.url;
  const { title } = interval;
  const speakers: Contact[] = flatten(
    (interval.sessions || []).map(({ people }) => people || [])
  );
  const people: Contact[] = interval.mc
    ? [interval.mc].concat(speakers)
    : speakers;

  return (
    <TweetContainer
      primaryColor={theme.colors.primary}
      secondaryColor={theme.colors.secondary}
      texture={theme.textures[0].url}
    >
      <TweetImageContainer>
        {(people.length > 4 ? people.slice(1, 5) : people).map(
          ({ name, image }) => (
            <TweetImage
              key={name}
              src={image.url}
              borderColor={theme.colors.background}
            />
          )
        )}
        {people.length > 4 && (
          <Centered>
            <TweetImage
              src={people[0].image.url}
              borderColor={theme.colors.background}
            />
          </Centered>
        )}
      </TweetImageContainer>
      <TweetInfoContainer>
        <TweetRow>
          <TweetLogo src={logo} />
          <TweetConferenceDays>{days}</TweetConferenceDays>
        </TweetRow>
        <TweetText>{title}</TweetText>
        <TweetDescription fontFamily={theme.fonts.primary}>
          {people.map(({ name }, i) => (
            <span key={name}>
              <span style={{ whiteSpace: "nowrap" }}>{name}</span>
              {i < people.length - 1 && <span>, </span>}
            </span>
          ))}
        </TweetDescription>
      </TweetInfoContainer>
    </TweetContainer>
  );
}

const ConnectedSpeakerTweetTemplate = connect(
  "/graphql",
  `
query SpeakerTweetTemplateQuery($conferenceId: ID!, $intervalTitle: String!) {
  interval(conferenceId: $conferenceId, intervalTitle: $intervalTitle) {
    begin
    end
    title
    mc {
      name
      image {
        url
      }
    }
    sessions {
      title
      description
      people {
        name
        image {
          url
        }
      }
    }
  }
  conference(id: $conferenceId) {
    name
    slogan
    schedules {
      day
    }
    locations {
      city
      country {
        name
      }
    }
  }
}
  `,
  {},
  ({ conferenceId, intervalTitle }) => ({ conferenceId, intervalTitle })
)(SpeakerTweetTemplate);

ConnectedSpeakerTweetTemplate.filename = "speaker-tweet";

// TODO: Better use enums here
ConnectedSpeakerTweetTemplate.variables = [
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
    id: "intervalTitle",
    query: `query SessionQuery($conferenceId: ID!) {
  conference(id: $conferenceId) {
    schedules {
      intervals {
        title
      }
    }
  }
}`,
    mapToCollection({ conference }) {
      return []
        .concat(
          ...get(conference, "schedules", []).map(({ intervals }) => intervals)
        )
        .filter(({ title }) => title);
    },
    mapToOption({ title }) {
      return { value: title, label: title };
    },
  },
];

export default ConnectedSpeakerTweetTemplate;
