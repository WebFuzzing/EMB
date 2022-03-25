import styled from "@emotion/styled";
import Markdown from "markdown-to-jsx";
import modularScale from "polished/lib/helpers/modularScale";
import React from "react";
import { Image } from "../../../schema/Image";
import { Theme } from "../../../schema/Theme";
import excludeProps from "../exclude-props";
import getBackground from "./get-background";

const GridContainer = styled(excludeProps(["background"], "div"))`
  min-height: 100vh;
  max-height: 100vh;
  display: grid;
  grid-template-rows: 0.25fr 1.75fr;
  align-items: center;
  line-height: 1.5;
  background: ${({ background }) => background};
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center;
`;

const Title = styled.h1`
  font-size: ${modularScale(6)};
  margin-left: 5vw;
  margin-right: 5vw;
  color: ${props => props.color};
`;

const Markup = styled(excludeProps(["color"], "div"))`
  display: grid;
  grid-template-columns: 1fr 1fr;
  font-size: ${modularScale(4)};
  margin-left: 5vw;
  margin-right: 5vw;
  align-self: start;
  color: ${props => props.color};
  opacity: 0.8;
`;

const MarkdownContainer = styled.div`
  margin: 0.5em;
`;

export interface GridContent {
  columns: [string, string]; // TODO: Generalize
  title: string;
}

interface GridContentProps {
  background: Image;
  content: GridContent;
  theme: Theme;
}

const GridContent = ({ background, content, theme }: GridContentProps) => (
  <GridContainer background={getBackground(background)}>
    <Title color={theme.colors.primary}>
      <Markdown>{content.title}</Markdown>
    </Title>
    <Markup className="markup" color={theme.colors.secondary}>
      <MarkdownContainer>
        <Markdown>{content.columns[0]}</Markdown>
      </MarkdownContainer>
      <MarkdownContainer>
        <Markdown>{content.columns[1]}</Markdown>
      </MarkdownContainer>
    </Markup>
  </GridContainer>
);

export default GridContent;
