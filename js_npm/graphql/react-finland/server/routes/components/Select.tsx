import styled from "@emotion/styled";
import { WidthProperty } from "csstype";
import * as React from "react";

interface SelectContainerProps {
  width?: any;
  onChange?: (event: React.ChangeEvent<HTMLSelectElement>) => void;
  value: HTMLSelectElement["value"];
}

const SelectContainer = styled.select`
  width: ${({ width }: SelectContainerProps) => width || ""};
` as React.FC<SelectContainerProps>;

// TODO: Add generics
// TODO: Drop selected from options?
interface SelectProps {
  width?: WidthProperty<string>;
  onChange: React.SelectHTMLAttributes<HTMLSelectElement>["onChange"];
  options: Array<{ value: any; label: any; selected?: boolean }>;
  selected?: any; // One of options
}

function Select({ width, onChange, options, selected }: SelectProps) {
  return (
    <SelectContainer
      width={width}
      onChange={onChange}
      value={selected || getSelectedValue(options)}
    >
      {options.map(({ value, label }) => (
        <option key={value} value={value}>
          {label}
        </option>
      ))}
    </SelectContainer>
  );
}

function getSelectedValue(options) {
  if (options.length <= 0) {
    return;
  }
  const selectedOptions = options.filter(({ selected }) => selected);

  if (selectedOptions.length <= 0) {
    return;
  }

  // TODO: This picks just the first one. Likely we should not even do this.
  return selectedOptions[0].value;
}

export default Select;
