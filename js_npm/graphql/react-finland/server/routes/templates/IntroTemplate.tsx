import styled from "@emotion/styled";
import { Color, WidthProperty } from "csstype";
import hexToRgba from "hex-to-rgba";
import desaturate from "polished/lib/color/desaturate";
import * as React from "react";
import { Theme } from "../../schema/Theme";
import connect from "../components/connect";
import Contacts from "../components/Contacts";
import Presentation from "../components/Presentation";
import Sponsor from "../components/Sponsor";
import sponsorQuery from "../queries/sponsorQuery";

interface SchedulePageContainerProps {
  id: string;
  primaryColor: Color;
  secondaryColor: Color;
  texture: string;
  sideBarWidth: WidthProperty<string>;
}

const IntroTemplateContainer = styled.article`
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
  width: ${({ sideBarWidth }) =>
    sideBarWidth ? `calc(100vw - ${sideBarWidth})` : "100vw"};
  overflow: hidden;
` as React.FC<SchedulePageContainerProps>;

interface IntroTemplateProps {
  theme: Theme;
  conferenceId: string;
  id: string;
  sideBarWidth: WidthProperty<string>;
}

function IntroTemplate({ theme, id, sideBarWidth }: IntroTemplateProps) {
  return (
    <IntroTemplateContainer
      id={id}
      primaryColor={theme.colors.primary}
      secondaryColor={theme.colors.secondary}
      texture={theme.textures[0].url}
      sideBarWidth={sideBarWidth}
    >
      <Presentation
        presentationID="intro-presentation"
        slides={getSlides(theme)}
        theme={theme}
        features={{
          showSlideNumber: false,
          showSlideProgress: false,
        }}
      />
    </IntroTemplateContainer>
  );
}

// TODO: Refactor so that `Sponsor` accepts a css parameter
const sponsorRules = {
  goldSponsors: {
    "max-height": "8cm",
    "max-width": "14cm",
    margin: "1cm",
  },
  silverSponsors: {
    "max-height": "3cm",
    "max-width": "5cm",
    margin: "1cm",
  },
  bronzeSponsors: {
    "max-height": "1.5cm",
    "max-width": "3.5cm",
    margin: "1cm",
  },
  partners: {
    "max-height": "3cm",
    "max-width": "5cm",
    margin: "1cm",
  },
};

const SponsorsContainer = styled.div`
  height: 100%;
  width: 100%;
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  background-color: #ffffffba;
  justify-items: center;
  align-items: center;
`;

const SponsorContainer = styled.div`
  height: 100%;
  width: 100%;
  display: grid;
  grid-template-columns: 1fr;
  background-color: #ffffffba;
  justify-items: center;
  align-items: center;
`;

const SilverSponsorsContainer = styled.div`
  height: 100%;
  width: 100%;
  display: grid;
  grid-template-columns: 1fr 1fr 1fr 1fr 1fr;
  background-color: #ffffffba;
  justify-items: center;
  align-items: center;
`;

const PartnersContainer = styled.div`
  height: 100%;
  width: 100%;
  grid-template-columns: 1fr 1fr 1fr 1fr;
  display: grid;
  background-color: #ffffffba;
  justify-items: center;
  align-items: center;
`;

const connectSponsor = (type: string) =>
  connect(
    "/graphql",
    sponsorQuery,
    ({ conferenceId }) => ({ conferenceId })
  )(({ conference, index }) => (
    <Contacts
      items={conference && conference[type] && [conference[type][index]]}
      render={Sponsor}
      renderProps={{ rules: sponsorRules, type }}
    />
  ));

const connectSponsors = (type: string) =>
  connect(
    "/graphql",
    sponsorQuery,
    ({ conferenceId }) => ({ conferenceId })
  )(({ conference }) => (
    <Contacts
      items={conference && conference[type]}
      render={Sponsor}
      renderProps={{ rules: sponsorRules, type }}
    />
  ));

// const ConnectedGoldSponsors = connectSponsors("goldSponsors");
const ConnectedGoldSponsor = connectSponsor("goldSponsors");
const ConnectedSilverSponsors = connectSponsors("silverSponsors");
const ConnectedBronzeSponsors = connectSponsors("bronzeSponsors");
const ConnectedPartners = connectSponsors("partners");

function getSlides(theme) {
  const titleSlide = [
    {
      layout: "IMAGE",
      content: {
        url: theme.logos.white.withText.url,
      },
      css: {
        backgroundImage: `linear-gradient(${theme.colors.primary}, ${desaturate(
          0.2,
          hexToRgba(theme.colors.secondary, 0.79)
        )}), url("${theme.textures[0].url}")`,
        backgroundSize: "cover",
      },
    },
    {
      layout: "TITLE",
      content: {
        title: (
          <>
            "Professional" MCs - <b>@jevakallio</b> and <b>@NikkitaFTW</b>
          </>
        ),
      },
    },
    {
      layout: "IMAGE",
      content: {
        url: "https://imgoat.com/uploads/8baa56554f/141386.jpeg",
      },
    },
    {
      layout: "TITLE",
      content: {
        title: "~400 people",
      },
    },
    {
      layout: "TITLE",
      content: {
        title: "Six workshops",
      },
    },
    {
      layout: "TITLE",
      content: {
        title: "27 speakers",
      },
    },
    {
      layout: "TITLE",
      content: {
        title: "Two days",
      },
    },
    {
      layout: "TITLE",
      content: {
        title: "Single track",
      },
    },
    {
      layout: "TITLE",
      content: {
        title: "Themed sessions",
      },
    },
    {
      layout: "MARKDOWN",
      background: {
        url: "/media/assets/disco.jpg",
      },
      content: {
        title: "Two parties",
        markup: `
* #SMOOSH
* Afterparty
        `,
      },
    },
    {
      layout: "REACT",
      content: (
        <SponsorContainer>
          <ConnectedGoldSponsor conferenceId="react-finland-2019" index={0} />
        </SponsorContainer>
      ),
    },
    {
      layout: "REACT",
      content: (
        <SponsorContainer>
          <ConnectedGoldSponsor conferenceId="react-finland-2019" index={1} />
        </SponsorContainer>
      ),
    },
    {
      layout: "REACT",
      content: (
        <SponsorContainer>
          <ConnectedGoldSponsor conferenceId="react-finland-2019" index={2} />
        </SponsorContainer>
      ),
    },
    {
      layout: "REACT",
      content: (
        <SilverSponsorsContainer>
          <ConnectedSilverSponsors conferenceId="react-finland-2019" />
        </SilverSponsorsContainer>
      ),
    },
    {
      layout: "REACT",
      content: (
        <SponsorsContainer>
          <ConnectedBronzeSponsors conferenceId="react-finland-2019" />
        </SponsorsContainer>
      ),
    },
    {
      layout: "REACT",
      content: (
        <PartnersContainer>
          <ConnectedPartners conferenceId="react-finland-2019" />
        </PartnersContainer>
      ),
    },
    {
      layout: "TITLE",
      content: {
        title: "Remember to join the conference Slack",
      },
    },
    {
      layout: "MARKDOWN",
      content: {
        title: "Code of Conduct",
        markup: `
* Berlin Code of Conduct
* Report to info@react-finland.fi
* Or to any org with RF logo shirt`,
      },
    },
    {
      layout: "IMAGE",
      content: {
        url: "/media/assets/red-strings.jpg",
      },
    },
    {
      layout: "TITLE",
      content: {
        title: "#ReactFinland",
      },
    },
  ];

  return titleSlide;
}

export default IntroTemplate;
