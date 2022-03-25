import styled from "@emotion/styled";
import isEqual from "lodash/isEqual";
import React from "react";
import scrollIntoView from "smooth-scroll-into-view-if-needed";
import root from "window-or-global";
import { Theme } from "../../schema/Theme";
import Slides, { Slide, SlidesProps } from "./Slides";

const PresenterContainer = styled.div``;

interface PresentationProps {
  presentationID: string;
  slides: Slide[];
  theme: Theme;
  features: SlidesProps["features"];
}

class Presentation extends React.Component<PresentationProps, {}> {
  public scrollTimeout = null;
  public slide = getSlide();

  public componentDidCatch(err) {
    // TODO: Use a nice error overlay here
    console.error(err);
  }
  public componentDidMount() {
    if (root.document) {
      root.document.addEventListener("keydown", this.onKeydown, false);
      root.addEventListener("wheel", this.onScroll);

      if (!root.location.hash) {
        root.location.hash = 0;
      }
    }
  }
  public componentWillUnmount() {
    if (root.document) {
      root.document.removeEventListener("keydown", this.onKeydown, false);
      root.removeEventListener("wheel", this.onScroll);
      root.clearTimeout(this.scrollTimeout);
    }
  }
  public componentDidUpdate(nextProps) {
    if (!isEqual(this.props.slides, nextProps.slides)) {
      this.scrollToSlide(this.slide);
    }
  }

  public onKeydown = event => {
    const { key } = event;

    if (key === "ArrowUp" || key === "ArrowLeft") {
      event.preventDefault();
      this.moveToPreviousSlide();
    }
    if (key === "ArrowDown" || key === "ArrowRight") {
      event.preventDefault();
      this.moveToNextSlide();
    }
  };
  public onScroll = () => {
    const scrollY = root.scrollY;
    const slideHeight = getSlideHeight();
    const nearestSlide = Math.round(scrollY / slideHeight);

    root.location.hash = nearestSlide;

    root.clearTimeout(this.scrollTimeout);
    this.scrollTimeout = root.setTimeout(() => {
      if (scrollY === root.scrollY) {
        this.scrollToSlide(nearestSlide);
      }
    }, 100);
  };

  public moveToPreviousSlide = () => {
    this.goToSlide(Math.max(Number(root.location.hash.slice(1)) - 1, 0));
  };

  public moveToNextSlide = () => {
    this.goToSlide(
      Math.min(
        Number(root.location.hash.slice(1)) + 1,
        this.props.slides.length - 1
      )
    );
  };

  public goToSlide = slide => {
    this.slide = slide;
    this.scrollToSlide(slide);
  };

  public scrollToSlide(slide) {
    const element = root.document.getElementsByClassName(`slide-${slide}`)[0];

    scrollIntoView(element).then(() => {
      root.location.hash = slide;
    });
  }

  public render() {
    const { features, presentationID, slides, theme } = this.props;

    return (
      <PresenterContainer>
        <Slides
          selectedSlide={this.slide}
          slides={slides}
          theme={theme}
          presentationID={presentationID}
          features={features}
        />
      </PresenterContainer>
    );
  }
}

function getSlide() {
  if (!root.location) {
    return 0;
  }

  return root.location.hash ? Number(root.location.hash.slice(1)) : 0;
}

// Assumes there's at least one slide
function getSlideHeight() {
  const element = root.document.getElementsByClassName(`slide-0`)[0];

  if (element) {
    return element.offsetHeight;
  }

  return 0;
}

export default Presentation;
