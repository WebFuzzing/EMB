/** @jsx jsx */
import { jsx } from "@emotion/core";
import styled from "@emotion/styled";
import { Theme } from "../../../schema/Theme";

const ReactPageContainer = styled.div`
  min-height: 100vh;
  max-height: 100vh;
  display: grid;
  align-items: center;
  justify-items: center;
`;

interface ReactContentProps {
  content: JSX.Element;
  theme: Theme;
  css: {};
}

function ReactContent({ content, css }: ReactContentProps) {
  return <ReactPageContainer css={css}>{content}</ReactPageContainer>;
}

export default ReactContent;
