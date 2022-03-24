import styled from "@emotion/styled";
import modularScale from "polished/lib/helpers/modularScale";
import React from "react";
import { Theme } from "../../../schema/Theme";

const EmbedPageContainer = styled.div`
  min-height: 100vh;
  max-height: 100vh;
  display: grid;
  grid-template-rows: 0.25fr 1.75fr;
  align-items: center;
`;

const Title = styled.h1`
  font-size: ${modularScale(7)};
  margin-left: 5vw;
  color: ${props => props.color};
`;

const Embed = styled.iframe`
  align-self: start;
  justify-self: center;
`;

export interface EmbedContent {
  title: string;
  link: string;
}

interface EmbedContentProps {
  content: EmbedContent;
  theme: Theme;
}

const EmbedContent = ({ content, theme }: EmbedContentProps) => (
  <EmbedPageContainer>
    <Title color={theme.colors.primary}>{content.title}</Title>
    <Embed width="90%" height="80%" src={content.link} frameBorder="0" />
  </EmbedPageContainer>
);

export default EmbedContent;
