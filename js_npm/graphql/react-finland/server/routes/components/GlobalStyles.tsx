import { css, Global } from "@emotion/core";
import forEach from "lodash/forEach";
import * as React from "react";
import { Font, Fonts } from "../../schema/Theme";

interface GlobalStylesProps {
  fonts: Fonts;
}

function GlobalStyles({ fonts }: GlobalStylesProps) {
  React.useEffect(() => {
    forEach(fonts.variants, (variant) => {
      if (variant.href) {
        generateFontReference(variant.href);
      }
    });
  }, []);

  return (
    <Global
      styles={css`
        ${generateFontDeclarations(fonts.variants)}

        * {
          box-sizing: border-box;

          /* Force accurate printing in Chrome */
          /* https://developer.mozilla.org/en-US/docs/Web/CSS/-webkit-print-color-adjust */
          -webkit-print-color-adjust: exact;
        }

        body {
          font-family: "${fonts.primary}", sans-serif;
          background: "white";
        }

        h1,
        h2,
        h3,
        h4,
        html,
        body {
          padding: 0;
          margin: 0;
        }
      `}
    />
  );
}

function generateFontReference(href: Font["href"]) {
  if (!href) {
    return;
  }

  const fontLink = document.createElement("link");

  fontLink.href = href;

  fontLink.rel = "stylesheet";
  fontLink.type = "text/css";
  fontLink.crossOrigin = "anonymous";

  document.body.appendChild(fontLink);
}

function generateFontDeclarations(fonts: Font[]) {
  return fonts.map(generateFontDeclaration).join("\n");
}

function generateFontDeclaration(font: Font) {
  if (font.href) {
    return "";
  }

  return `
@font-face {
  font-family: "${font.family}";
  font-weight: ${font.weight};
  font-style: ${font.style};
  src: ${generateFontSrc(font.fileName, font.formats)}
}`;
}

function generateFontSrc(
  fileName: Font["fileName"],
  formats: Font["formats"]
): string {
  if (!fileName || !formats) {
    return "";
  }

  return formats.map(generateFontUrl(fileName)).join(",\n");
}

function generateFontUrl(fileName: string) {
  return (format: string): string =>
    `url("${fileName}.${format}") format("${generateFormat(format)}")`;
}

function generateFormat(format: string): string {
  if (format === "ttf") {
    return "truetype";
  }

  if (format === "otf") {
    return "opentype";
  }

  return format;
}

export default GlobalStyles;
