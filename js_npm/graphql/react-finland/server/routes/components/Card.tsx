import styled from "@emotion/styled";
import { Color } from "csstype";
import * as React from "react";

interface CardContainerProps {
  width: string;
  height: string;
}

interface CardBaseProps {
  backgroundColor?: Color;
  textColor?: Color;
}

const CardBase = styled.div`
  position: absolute;

  width: 100%;
  height: 100%;
  background-color: ${({ backgroundColor }: CardBaseProps) => backgroundColor};
  color: ${({ textColor }: CardBaseProps) => textColor};
  backface-visibility: hidden;
` as React.FC<CardBaseProps>;

const Card = {
  Container: styled.section`
    width: ${({ width }: CardContainerProps) => width};
    height: ${({ height }: CardContainerProps) => height};

    /* Animation */
    perspective: 100%;
    transform-style: preserve-3d;
    transition-duration: 500ms;

    &:hover {
      &:nth-of-type(1) {
        transform: rotateY(180deg);
      }

      &:nth-of-type(2) {
        transform: rotateY(0deg);
      }
    }
  ` as React.FC<CardContainerProps>,
  Front: styled(CardBase)``,
  Back: styled(CardBase)`
    transform: rotateY(-180deg);

    @media print {
      display: none;
    }
  `,
};

export default Card;
