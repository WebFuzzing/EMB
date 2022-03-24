import styled from "@emotion/styled";
import * as React from "react";
import { Contact } from "../../schema/Contact";
import Contacts from "./Contacts";
import Sponsor from "./Sponsor";

const SponsorsContainer = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  align-items: center;
  justify-items: center;
  margin-left: 0.5cm;
  margin-right: 0.5cm;
`;

const GoldSponsors = styled.section``;
const SilverSponsors = styled.section`
  display: grid;
  align-items: center;
  grid-template-columns: 1fr 1fr;
`;
const BronzeSponsors = styled.section`
  display: grid;
  align-items: center;
  grid-template-columns: 1fr 1fr 1fr;
`;

interface SponsorsProps {
  goldSponsors?: Contact[];
  silverSponsors?: Contact[];
  bronzeSponsors?: Contact[];
  rules: {};
}

// TODO: Check if the structure can be simplified
const Sponsors = ({
  goldSponsors = [],
  silverSponsors = [],
  bronzeSponsors = [],
  rules,
}: SponsorsProps) => (
  <SponsorsContainer>
    <GoldSponsors>
      <Contacts
        items={goldSponsors}
        render={Sponsor}
        renderProps={{ rules, type: "gold" }}
      />
    </GoldSponsors>
    <SilverSponsors>
      <Contacts
        items={silverSponsors}
        render={Sponsor}
        renderProps={{ rules, type: "silver" }}
      />
    </SilverSponsors>
    <BronzeSponsors>
      <Contacts
        items={bronzeSponsors}
        render={Sponsor}
        renderProps={{ rules, type: "bronze" }}
      />
    </BronzeSponsors>
  </SponsorsContainer>
);

export default Sponsors;
