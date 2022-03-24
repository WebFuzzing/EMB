import styled from "@emotion/styled";
import { Color, HeightProperty, WidthProperty } from "csstype";
import hexToRgba from "hex-to-rgba";
import get from "lodash/get";
import trimStart from "lodash/trimStart";
import * as React from "react";
import { Contact, ContactType } from "../../schema/Contact";
import { Theme } from "../../schema/Theme";
import Card from "../components/Card";

interface BadgeContainerProps {
  width: string;
  height: string;
}

// TODO: Expose dimensions?
// TODO: Use Card.Container
const BadgeContainer = styled.section`
  display: grid;
  grid-template-rows: repeat(3, 1fr);

  /* Animation */
  perspective: 100%;
  transform-style: preserve-3d;
  transition-duration: 500ms;

  margin: 0;
  padding: 0;
  width: ${({ width }: BadgeContainerProps) => width};
  height: ${({ height }: BadgeContainerProps) => height};

  /* Hole for lanyard */
  &::after {
    position: relative;
    top: -14.2cm;
    left: 50%;
    width: 1mm;
    height: 1mm;
    background: rgba(151, 54, 54, 1);
    border-radius: 50%;
    content: "";
    z-index: 5;
  }
` as React.FC<BadgeContainerProps>;

interface BadgeBaseProps {
  defaultColor: Color;
  type: ContactType;
  texture: string;
  opacity?: {
    upper: number;
    lower: number;
  };
}

const BadgeFront = styled(Card.Front)`
  border-radius: 0.5cm;
  background-image: ${resolveBackground};
  background-size: cover;
` as React.FC<BadgeBaseProps>;
/*const BadgeBack = styled(Card.Back)`
  border-radius: 0.5cm;
  background-image: ${resolveBackground};
  background-size: cover;
` as React.FC<BadgeBaseProps>;*/

function resolveBackground({
  defaultColor = "#000",
  type,
  texture,
  opacity = {
    upper: 1.0,
    lower: 0.6,
  },
}: BadgeBaseProps) {
  const colors = {
    [ContactType.ATTENDEE]: defaultColor,
    [ContactType.ORGANIZER]: "#6d0b4d",
    [ContactType.PRESS]: "#e5ce3b",
    [ContactType.SPEAKER]: "#d01a1a",
    [ContactType.WORKSHOP_HOST]: "#d01a1a",
    [ContactType.SPONSOR]: "#67d67b",
  };
  const color = colors[type];

  if (!color) {
    console.log(`Badge - Missing color for ${type}`);

    return defaultColor;
  }

  return `linear-gradient(${hexToRgba(color, opacity.upper)}, ${hexToRgba(
    color,
    opacity.lower
  )}), url("${texture}")`;
}

const BadgeContent = styled.div`
  margin-left: 0.2cm;
  margin-right: 0.2cm;
  padding: 2cm 0.5cm;
  height: 8.5cm;
  clip-path: polygon(0 0, 100% 0.5cm, 100% 100%, 0 calc(100% - 0.5cm));
  background: ${hexToRgba("#fff", 0.8)};
  z-index: 1;
`;

const BadgeLogo = styled.img`
  margin-top: 0.5em;
`;

const BadgeName = styled.h2`
  display: block;
  font-size: 135%;
`;

const BadgeTwitter = styled.h3`
  font-weight: bold;
  margin-top: 1em;
  margin-bottom: 1em;
  font-size: 125%;
`;

const BadgeCompany = styled.p`
  font-weight: bold;
`;

const BadgeFooter = styled.footer`
  padding-left: 1cm;
  justify-self: start;
`;

const BadgeType = styled.h3`
  text-transform: capitalize;
  text-align: initial;
  color: white;
`;

interface BadgeProps {
  defaultColor: Color;
  logo: Theme["logos"]["white"]["withText"]["url"];
  attendee: Contact;
  texture: string;
  height: HeightProperty<string>;
  width: WidthProperty<string>;
}

// TODO: Use Image type for logo, not url (string)
function Badge({
  defaultColor,
  logo,
  texture,
  attendee,
  width,
  height,
}: BadgeProps) {
  if (!attendee) {
    return <>No attendee!</>;
  }

  const { type, name, social, company } = attendee;
  const twitter = get(social, "twitter", "");

  // TODO: Drop the trimStart bit -> Needs a schema change
  const frontContent = (
    <>
      <BadgeLogo src={logo} />
      <BadgeContent>
        <BadgeName>{name}</BadgeName>
        {trimStart(twitter, "https://twitter.com/") && (
          <BadgeTwitter>
            @{trimStart(twitter, "https://twitter.com/")}
          </BadgeTwitter>
        )}
        {company && <BadgeCompany>{company}</BadgeCompany>}
      </BadgeContent>
      {type && (
        <BadgeFooter>
          <BadgeType>{type[0]}</BadgeType>
        </BadgeFooter>
      )}
    </>
  );
  // TODO: Expose
  // const backContent = frontContent;

  // TODO: Eliminate BadgeContainer?
  // TODO: What if an attendee has multiple types at once?
  return (
    <BadgeContainer width={width} height={height}>
      <BadgeFront defaultColor={defaultColor} texture={texture} type={type[0]}>
        {frontContent}
      </BadgeFront>
      {/*<BadgeBack defaultColor={defaultColor} texture={texture} type={type[0]}>
        {backContent}
  </BadgeBack>*/}
    </BadgeContainer>
  );
}

// TODO: Generate Badge.propTypes based on this structure?
Badge.variables = [
  {
    id: "defaultColor",
    validation: { type: String, default: "#002fa9" },
  },
  {
    id: "logo",
    validation: {
      type: String,
      default: "/media/react-finland/logo/v2/logo-white-with-text.svg",
    },
  },
  {
    id: "attendee",
    validation: {
      type: Contact,
      default: {
        type: [ContactType.ATTENDEE],
        name: "John Doe",
        twitter: "johndoe",
        company: "John Doe Co.",
        noPhotography: false,
      },
    },
  },
  {
    id: "texture",
    validation: { type: String, default: "/media/assets/wave.svg" },
  },
];

export default Badge;
