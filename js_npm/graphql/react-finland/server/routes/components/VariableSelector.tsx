import styled from "@emotion/styled";
import get from "lodash/get";
import map from "lodash/map";
import * as React from "react";
import connect from "./connect";
import Select from "./Select";

const SelectorLabel = styled.label`
  margin-right: 1em;
`;
const SelectorInput = styled.input`
  width: 100%;
`;
const SelectorTextArea = styled.textarea`
  width: 100%;
`;
const SelectorContainer = styled.div`
  display: grid;
  grid-template-columns: 0.75fr 1.25fr;
`;

interface VariableSelectorProps {
  variables: { [key: string]: any };
  field: string;
  selectedVariable: string;
  query: string;
  // TODO: Use the same type as in connect
  mapToCollection: (result: any) => any;
  mapToOption: (result: any) => { value: any; label: any };
  validation: { type: any; default: string };
  onChange: (field: string, value: string) => void;
}

function VariableSelector({
  field,
  selectedVariable,
  query,
  mapToCollection,
  mapToOption,
  validation,
  onChange,
  variables,
}: VariableSelectorProps) {
  if (!query) {
    return (
      <VariableFields
        field={field}
        validation={validation}
        selectedVariable={selectedVariable}
        onChange={onChange}
      />
    );
  }

  const ConnectedSelect = connect(
    "/graphql",
    query,
    variables
  )(result => {
    const collection = mapToCollection(result);

    return (
      <SelectorContainer>
        <SelectorLabel>{field}</SelectorLabel>
        <Select
          width="100%"
          options={
            collection
              ? [{ value: "", label: "" }].concat(collection.map(mapToOption))
              : []
          }
          selected={selectedVariable}
          onChange={({ target: { value } }) => {
            onChange(field, value);
          }}
        />
      </SelectorContainer>
    );
  });

  return <ConnectedSelect />;
}

function VariableFields({ validation, selectedVariable, onChange, field }) {
  if (validation.type === String) {
    // TODO: Use an enum here instead
    if (validation.modifier === "long") {
      return (
        <SelectorContainer>
          <SelectorLabel>{field}</SelectorLabel>
          <SelectorTextArea
            defaultValue={selectedVariable}
            onChange={({ target: { value } }) => {
              onChange(field, value);
            }}
            rows={8}
          />
        </SelectorContainer>
      );
    }

    return (
      <SelectorContainer>
        <SelectorLabel>{field}</SelectorLabel>
        <SelectorInput
          type="text"
          defaultValue={selectedVariable}
          onChange={({ target: { value } }) => {
            onChange(field, value);
          }}
        />
      </SelectorContainer>
    );
  }
  if (validation.type === Number) {
    return (
      <SelectorContainer>
        <SelectorLabel>{field}</SelectorLabel>
        <SelectorInput
          type="number"
          defaultValue={selectedVariable}
          onChange={({ target: { value } }) => {
            onChange(field, Number(value));
          }}
        />
      </SelectorContainer>
    );
  }
  if (validation.type === Boolean) {
    return (
      <SelectorContainer>
        <SelectorLabel>{field}</SelectorLabel>
        <SelectorInput
          type="checkbox"
          checked={selectedVariable}
          onChange={({ target: { checked } }) => {
            onChange(field, checked);
          }}
        />
      </SelectorContainer>
    );
  }
  if (validation.type === "enum") {
    const values = validation.values;

    return (
      <SelectorContainer>
        <SelectorLabel>{field}</SelectorLabel>
        <Select
          width="100%"
          options={
            values
              ? [{ value: "", label: "" }].concat(
                  /* TODO: This assumes uniqueness */
                  map(values, label => ({
                    value: label,
                    label,
                  }))
                )
              : []
          }
          selected={selectedVariable}
          onChange={({ target: { value } }) => {
            onChange(field, value);
          }}
        />
      </SelectorContainer>
    );
  }

  const fields = validation.type._fields[validation.type.name];

  if (fields) {
    const validationDefaults = validation.default;

    return (
      <>
        {map(fields, ({ type, values }, id) => (
          <VariableFields
            key={id}
            validation={{
              id,
              type,
              values,
            }}
            selectedVariable={
              get(validationDefaults, id) || get(selectedVariable, id)
            }
            onChange={onChange}
            field={`${field}.${id}`}
          />
        ))}
      </>
    );
  }

  console.error(`Type ${validation.type} hasn't been implemented yet`);
  return null;
}

export default VariableSelector;
