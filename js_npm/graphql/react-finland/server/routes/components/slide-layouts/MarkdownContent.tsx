import styled from "@emotion/styled";
import Markdown from "markdown-to-jsx";
import modularScale from "polished/lib/helpers/modularScale";
import React from "react";
import { Image } from "../../../schema/Image";
import { Theme } from "../../../schema/Theme";
import excludeProps from "../exclude-props";
import getBackground from "./get-background";

// TODO: Allow background-size to be tuneable (contain is needed sometimes)
const MarkdownContainer = styled(excludeProps(["background", "title"], "div"))`
  min-height: 100vh;
  max-height: 100vh;
  display: grid;
  grid-template-rows: ${({ title }) => (title ? "0.25fr 1.75fr" : "1fr")};
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

const Markup = styled(excludeProps(["color", "title"], "div"))`
  font-size: ${modularScale(4)};
  margin-left: 5vw;
  margin-right: 5vw;
  align-self: ${({ title }) => (title ? "start" : "center")};
  color: ${({ color }) => color};
  opacity: 0.8;
`;

export interface MarkdownContent {
  title: string;
  markup: string;
}

interface MarkdownContentProps {
  background: Image;
  content: MarkdownContent;
  theme: Theme;
}

const MarkdownContent = ({
  background,
  content,
  theme,
}: MarkdownContentProps) => (
  <MarkdownContainer
    background={getBackground(background)}
    title={content.title}
  >
    {content.title && (
      <Title color={theme.colors.text}>
        <Markdown>{content.title}</Markdown>
      </Title>
    )}
    <Markup className="markup" color={theme.colors.text} title={content.title}>
      <Markdown>{content.markup}</Markdown>
    </Markup>
  </MarkdownContainer>
);

export default MarkdownContent;
