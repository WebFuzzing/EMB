import omit from "lodash/omit";
import React from "react";

export default function excludeProps(exclude, element) {
  const ExcludedStyled = props =>
    React.createElement(element, omit(props, exclude));

  return ExcludedStyled;
}
