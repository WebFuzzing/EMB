/** @jsx jsx */
import { jsx } from "@emotion/core";
import styled from "@emotion/styled";
import { Image } from "../../../schema/Image";

const ImageContainer = styled.div`
  display: grid;
  min-height: 100vh;
  max-height: 100vh;
  text-align: center;
  padding: 2em;
  box-sizing: border-box;
`;

const ImageElement = styled.img`
  height: 100%;
  max-width: 100%;
  object-fit: contain;
  align-self: center;
  justify-self: center;
`;

interface ImageContentProps {
  content: Image;
  css: {};
}

const ImageContent = ({ content, css }: ImageContentProps) => (
  <ImageContainer css={css}>
    <ImageElement src={content.url} title={content.title || ""} height="100%" />
  </ImageContainer>
);

export default ImageContent;
