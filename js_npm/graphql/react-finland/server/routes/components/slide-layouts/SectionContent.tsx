import styled from "@emotion/styled";
import modularScale from "polished/lib/helpers/modularScale";
import React from "react";
import { Theme } from "../../../schema/Theme";
import excludeProps from "../exclude-props";

const SectionPageContainer = styled(excludeProps("backgroundColor", "div"))`
  min-height: 100vh;
  max-height: 100vh;
  display: grid;
  grid-template-rows: 1fr;
  align-items: center;
  background: ${props => props.background};
`;

const Title = styled.h1`
  font-size: ${modularScale(7)};
  margin-left: 10vw;
  color: ${props => props.color};
`;

export interface SectionContent {
  title: string;
}

interface SectionContentProps {
  content: SectionContent;
  theme: Theme;
}

const SectionContent = ({ content, theme }: SectionContentProps) => (
  <SectionPageContainer background={theme.colors.primary}>
    <Title color={theme.colors.background}>{content.title}</Title>
  </SectionPageContainer>
);

export default SectionContent;
