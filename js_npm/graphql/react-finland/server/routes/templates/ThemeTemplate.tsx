import styled from "@emotion/styled";
import { Color } from "csstype";
import map from "lodash/map";
import complement from "polished/lib/color/complement";
import invert from "polished/lib/color/invert";
import * as React from "react";
import { Font, Theme } from "../../schema/Theme";

interface ThemeTemplateProps {
  theme: Theme;
}

const ThemeTemplateContainer = styled.section``;
const ThemeTemplateHeader = styled.h1``;

const FontsContainer = styled.section`
  margin-top: 1em;
  margin-bottom: 1em;
`;
const FontsPrimaryHeader = styled.h2`
  margin-bottom: 0.5em;
`;
const FontsSecondaryHeader = styled.h3`
  margin-bottom: 0.5em;
`;

interface FontContainerProps {
  fontFamily: Font["family"];
}

const FontContainer = styled.div`
  font-family: "${({ fontFamily }: FontContainerProps) => fontFamily}";
` as React.FC<FontContainerProps>;

const TexturesContainer = styled.section`
  margin-top: 1em;
  margin-bottom: 1em;
`;
const TextureHeader = styled.h2`
  margin-bottom: 0.5em;
`;

interface TextureProps {
  src: HTMLImageElement["src"];
}
const Texture = styled.img`
  max-width: 8em;
  max-height: 8em;
` as React.FC<TextureProps>;

const ColorsContainer = styled.section`
  margin-top: 1em;
  margin-bottom: 1em;
`;
const ColorHeader = styled.h2`
  margin-bottom: 0.5em;
`;

interface ColorProps {
  color: Color;
}
const Color = styled.div`
  padding: 1em;
  display: inline-block;
  width: 8em;
  height: 8em;
  background-color: ${({ color }: ColorProps) => color};
  color: ${({ color }: ColorProps) =>
    tryTo(() => complement(invert(color)), "")};
` as React.FC<ColorProps>;

const LogosContainer = styled.section`
  margin-top: 1em;
  margin-bottom: 1em;
`;
const LogoHeader = styled.h2`
  margin-bottom: 0.5em;
`;

interface LogoProps {
  property: string;
  alt: HTMLImageElement["alt"];
  src: HTMLImageElement["src"];
}
const LogoContainer = styled.section`
  margin-right: 1em;
  display: inline-block;
  vertical-align: top;
  font-style: italic;
`;
const Logo = styled.img`
  max-width: 8em;
  max-height: 8em;
  background-color: ${({ property }: LogoProps) =>
    tryTo(() => invert(property), "")};
` as React.FC<LogoProps>;
const LogoLabel = styled.label``;

function tryTo(fn, defaultValue) {
  try {
    return fn();
  } catch (err) {
    return defaultValue;
  }
}

interface ThemeTemplateProps {
  theme: Theme;
  id: string;
}

function ThemeTemplate({ id, theme }: ThemeTemplateProps) {
  return (
    <ThemeTemplateContainer id={id}>
      <ThemeTemplateHeader>Theme</ThemeTemplateHeader>
      <FontsContainer>
        <FontsPrimaryHeader>Fonts</FontsPrimaryHeader>
        <FontsSecondaryHeader>Primary</FontsSecondaryHeader>
        <FontContainer fontFamily={theme.fonts.primary}>
          The quick brown fox jumps over the lazy dog
        </FontContainer>
        <FontsSecondaryHeader>Secondary</FontsSecondaryHeader>
        <FontContainer fontFamily={theme.fonts.secondary}>
          The quick brown fox jumps over the lazy dog
        </FontContainer>
      </FontsContainer>
      <TexturesContainer>
        <TextureHeader>Textures</TextureHeader>
        {map(theme.textures, (texture, index) => (
          <Texture key={index} src={texture.url} />
        ))}
      </TexturesContainer>
      <ColorsContainer>
        <ColorHeader>Colors</ColorHeader>
        {map(theme.colors, (color, colorProperty) => (
          <Color color={color} key={color}>
            {colorProperty}
          </Color>
        ))}
      </ColorsContainer>
      <LogosContainer>
        <LogoHeader>Logos</LogoHeader>
        {map(theme.logos, (logo, logoProperty) =>
          map(logo, (image, imageProperty) => {
            const property = `${logoProperty}.${imageProperty}`;

            return (
              <LogoContainer key={property}>
                <LogoLabel>{property}</LogoLabel>
                <Logo property={logoProperty} src={image.url} alt={property} />
              </LogoContainer>
            );
          })
        )}
      </LogosContainer>
    </ThemeTemplateContainer>
  );
}

ThemeTemplate.filename = "theme";

export default ThemeTemplate;
