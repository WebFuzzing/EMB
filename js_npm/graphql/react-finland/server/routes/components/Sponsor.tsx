import styled from "@emotion/styled";
import React from "react";
import { Image } from "../../schema/Image";

interface SponsorContentProps {
  src: string;
  name: string;
  props: {};
  rules: {};
  type: string; // TODO: Better use an enum here
}

const SponsorContent = styled.img`
  vertical-align: middle;
  justify-self: center;
  display: ${({ rules, type }: SponsorContentProps) => rules[type].display};
  max-height: ${({ rules, type }: SponsorContentProps) =>
    rules[type]["max-height"]};
  max-width: ${({ rules, type }: SponsorContentProps) =>
    rules[type]["max-width"]};
  margin: ${({ rules, type }: SponsorContentProps) => rules[type].margin};
` as React.FC<SponsorContentProps>;

interface SponsorProps {
  name: string;
  about: string;
  social: {};
  image: Image;
  logoProps: {};
  rules: {};
  type: string; // TODO: Better use the same enum here
}

const Sponsor = ({ name, image, logoProps, rules, type }: SponsorProps) => (
  <SponsorContent
    src={image.url}
    name={name}
    props={logoProps}
    rules={rules}
    type={type}
  />
);

export default Sponsor;
