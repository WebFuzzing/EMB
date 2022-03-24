import styled from "@emotion/styled";
import modularScale from "polished/lib/helpers/modularScale";
import React from "react";
import { Theme } from "../../../schema/Theme";

const TitlePageContainer = styled.div`
  min-height: 100vh;
  max-height: 100vh;
  display: grid;
  align-items: center;
  justify-items: center;
`;

const Title = styled.h1`
  font-size: ${modularScale(7)};
  color: ${props => props.color};
  padding: 8vw;
`;

/*const Subtitle = styled.h2`
  font-size: ${modularScale(4)};
  margin-right: 5vw;
  justify-self: end;
  color: ${props => props.color};
`;*/

export interface TitleContent {
  title: JSX.Element;
}

interface TitleContentProps {
  content: TitleContent;
  theme: Theme;
}

function TitleContent({ content, theme }: TitleContentProps) {
  return (
    <TitlePageContainer>
      <Title color={theme.colors.text}>{content.title}</Title>
    </TitlePageContainer>
  );
}

export default TitleContent;
