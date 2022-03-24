/** @jsx jsx */
import { jsx } from "@emotion/core";
import styled from "@emotion/styled";
import { Color, HeightProperty, WidthProperty } from "csstype";
import hexToRgba from "hex-to-rgba";
import map from "lodash/map";
import * as React from "react";
import { Conference } from "../../schema/Conference";
import { Theme } from "../../schema/Theme";
import connect from "../components/connect";
import { dayToFinnishLocale } from "../date-utils";
import conferenceDaysQuery from "../queries/conferenceDaysQuery";

interface HeaderContainerProps {
  id: string;
  primaryColor: Color;
  secondaryColor: Color;
  width: WidthProperty<string>;
  height: HeightProperty<string>;
  texture: string;
}

// TODO: Allow alpha to be defined for both
const HeaderPageContainer = styled.div`
  display: grid;
  background-image: ${({
    primaryColor,
    secondaryColor,
    texture,
  }: HeaderContainerProps) => `linear-gradient(
      ${primaryColor},
      ${hexToRgba(secondaryColor, 0.39)}
    ),
    url("${texture}")`};
  background-size: cover;
  position: relative;
  width: ${({ width }: HeaderContainerProps) => width};
  height: ${({ height }: HeaderContainerProps) => height};
  overflow: hidden;
  color: white;
` as React.FC<HeaderContainerProps>;

const TwitterSafeUser = styled.div`
  position: absolute;
  bottom: 0;
  background-color: #ff00008f;
  width: 450px;
  height: 200px;
  z-index: 1;
`;
const TwitterSafeTop = styled.div`
  position: absolute;
  top: 0;
  background-color: #ff00008f;
  width: 100%;
  height: 40px;
  z-index: 1;
`;
const TwitterSafeBottom = styled.div`
  position: absolute;
  bottom: 0;
  left: 450px;
  background-color: #ff00008f;
  width: 100%;
  height: 40px;
  z-index: 1;
`;

function TwitterSafeOverlay() {
  return (
    <React.Fragment>
      <TwitterSafeTop />
      <TwitterSafeBottom />
      <TwitterSafeUser />
    </React.Fragment>
  );
}

interface PrimaryRowProps {
  useTwitterHeader: boolean;
}

const PrimaryRow = styled.div`
  display: grid;
  grid-template-columns: ${({ useTwitterHeader }: PrimaryRowProps) =>
    useTwitterHeader ? "1fr 0.2fr" : "1fr 1fr"};
` as React.FC<PrimaryRowProps>;

interface SecondaryRowProps {
  useTwitterHeader: boolean;
}

const SecondaryRow = styled.div`
  display: grid;
  padding-left: ${({ useTwitterHeader }: SecondaryRowProps) =>
    useTwitterHeader ? "30em" : "5em"};
` as React.FC<SecondaryRowProps>;

const HeaderInfoContainer = styled.div`
  text-align: right;
  padding-right: 50px;
`;

interface HeaderLogoProps {
  src: string;
  useTwitterHeader: boolean;
}

const HeaderLogo = styled.img`
  padding-left: 3em;
  padding-right: 3em;
  padding-top: 1.8em;
  width: 100%;
  height: ${({ useTwitterHeader }: HeaderLogoProps) =>
    useTwitterHeader ? "20em" : ""};
` as React.FC<HeaderLogoProps>;

const HeaderConferenceDays = styled.h1`
  padding-top: 2em;
`;

const HeaderLocation = styled.h2`
  padding-top: 1em;
`;

const HeaderSlogan = styled.h2`
  padding-top: 1em;
`;

const HeaderCoupon = styled.h3`
  padding-top: 2em;
  font-family: "Courier New", Courier, monospace;
`;

enum HeaderType {
  WEB = "web",
  TWITTER = "twitter",
  A4 = "a4",
}

interface HeaderTemplateProps {
  conference?: Conference;
  theme: Theme;
  id: string;
  type: HeaderType;
  coupon?: string;
  discountPercentage?: string;
  showTwitterSafeArea: boolean;
  showDates: boolean;
  showLocation: boolean;
}

function HeaderTemplate({
  conference,
  theme,
  id,
  type,
  coupon,
  discountPercentage,
  showTwitterSafeArea,
  showDates,
  showLocation,
}: HeaderTemplateProps) {
  const { locations, schedules, slogan } = conference || {
    locations: [],
    schedules: [],
    slogan: "",
  };
  const location =
    locations && locations.length > 0 && locations[0]
      ? {
          city: locations[0].city,
          country: locations[0].country && locations[0].country.name,
        }
      : null;
  const conferenceDays = map(schedules, ({ day }) => dayToFinnishLocale(day));
  const firstDay = conferenceDays[0];
  const lastDay = conferenceDays[conferenceDays.length - 1];

  return (
    <HeaderPageContainer
      id={id}
      primaryColor={theme.colors.primary}
      secondaryColor={theme.colors.secondary}
      texture={theme.textures[0].url}
      width={resolveWidth(type)}
      height={resolveHeight(type)}
      css={theme.textures[0].style ? theme.textures[0].style : {}}
    >
      {showTwitterSafeArea && <TwitterSafeOverlay />}
      <PrimaryRow useTwitterHeader={type === HeaderType.TWITTER}>
        <HeaderLogo
          src={theme.logos.white.withText.url}
          useTwitterHeader={type === HeaderType.TWITTER}
        />
        <HeaderInfoContainer>
          {showDates && firstDay && lastDay && (
            <HeaderConferenceDays>
              {firstDay}-{lastDay}
            </HeaderConferenceDays>
          )}
          {showLocation && location && (
            <HeaderLocation>
              {location.city}, {location.country}
            </HeaderLocation>
          )}
        </HeaderInfoContainer>
      </PrimaryRow>
      <SecondaryRow useTwitterHeader={type === HeaderType.TWITTER}>
        <HeaderSlogan>{slogan}</HeaderSlogan>
        {coupon && (
          <HeaderCoupon>
            Use {coupon} for a {discountPercentage}% discount!
          </HeaderCoupon>
        )}
      </SecondaryRow>
    </HeaderPageContainer>
  );
}

function resolveWidth(type) {
  switch (type) {
    case HeaderType.TWITTER:
      return "1500px";
    case HeaderType.A4:
      return "297mm";
    case HeaderType.WEB:
    default:
      return "1024px";
  }
}

function resolveHeight(type) {
  switch (type) {
    case HeaderType.TWITTER:
      return "500px";
    case HeaderType.A4:
      return "210mm";
    case HeaderType.WEB:
    default:
      return "512px";
  }
}

const ConnectedHeaderTemplate = connect(
  "/graphql",
  conferenceDaysQuery,
  {},
  ({ conferenceId }) => ({ conferenceId })
)(HeaderTemplate);

ConnectedHeaderTemplate.filename = "header";

// TODO: Better use enums here
ConnectedHeaderTemplate.variables = [
  {
    id: "type",
    /* TODO: Fix default value */
    validation: {
      type: "enum",
      values: [HeaderType.WEB, HeaderType.TWITTER, HeaderType.A4],
      default: HeaderType.WEB,
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
  {
    id: "coupon",
    validation: { type: String, default: "" },
  },
  {
    id: "discountPercentage",
    validation: { type: String, default: "" },
  },
  {
    id: "showTwitterSafeArea",
    validation: { type: Boolean, default: false },
  },
  {
    id: "showDates",
    validation: { type: Boolean, default: true },
  },
  {
    id: "showLocation",
    validation: { type: Boolean, default: true },
  },
];

export default ConnectedHeaderTemplate;
