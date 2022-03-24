import styled from "@emotion/styled";
import React from "react";
// import { Image } from "../../schema/Image";
import { Theme } from "../../schema/Theme";
import excludeProps from "./exclude-props";
import slideLayouts from "./slide-layouts";
/*import { EmbedContent } from "./slide-layouts/EmbedContent";
import { GridContent } from "./slide-layouts/GridContent";
import { MarkdownContent } from "./slide-layouts/MarkdownContent";
import { SectionContent } from "./slide-layouts/SectionContent";
import { TitleContent } from "./slide-layouts/TitleContent";*/

const SlideContainer = styled.div``;
const Slide = styled.div``;

const SlideNumber = styled(excludeProps("index", "div"))`
  position: absolute;
  top: ${({ index }) => (index + 1) * 100 - 5}vh;
  right: 3vw;
  opacity: 0.8;
`;

const SlideProgress = styled(excludeProps(["color", "ratio"], "div"))`
  position: fixed;
  width: ${({ ratio }) => ratio * 100}%;
  height: 3vh;
  top: 97vh;
  background-color: ${props => props.color};
  opacity: 0.8;
`;

export interface Slide {
  layout: string; // TODO: Better as enum
  content: any;
  /*| EmbedContent
    | GridContent
    | Image
    | MarkdownContent
    | TitleContent
    | SectionContent;*/
}

export interface SlidesProps {
  selectedSlide: number;
  slides: Slide[];
  theme: Theme;
  presentationID: string;
  features: {
    showSlideNumber: boolean;
    showSlideProgress: boolean;
  };
}

function Slides({
  selectedSlide,
  slides = [],
  theme,
  presentationID,
  features,
}: SlidesProps) {
  return (
    <SlideContainer>
      {slides.map((slide, index) => {
        const slideKey = `slide-${index}`;

        // Slides are given class names for keyboard navigation to work.
        return (
          <Slide className={slideKey} key={slideKey}>
            {React.createElement(getLayout(slide.layout), {
              ...slide,
              theme,
              presentationID,
            })}
            {features.showSlideNumber && index ? (
              <SlideNumber index={index}>
                {index}/{slides.length - 1}
              </SlideNumber>
            ) : null}
          </Slide>
        );
      })}
      {features.showSlideProgress && selectedSlide ? (
        <SlideProgress
          color={theme.colors.secondary}
          ratio={selectedSlide / (slides.length - 1)}
        >
          &nbsp;
        </SlideProgress>
      ) : null}
    </SlideContainer>
  );
}

function getLayout(id) {
  if (!slideLayouts[id]) {
    throw new Error(`No layout found for ${id}`);
  }

  return slideLayouts[id];
}

export default Slides;
