import styled from "@emotion/styled";
import { Color, WidthProperty } from "csstype";
import { saveAs } from "file-saver";
import { createBrowserHistory as createHistory } from "history";
import fromPairs from "lodash/fromPairs";
import get from "lodash/get";
import map from "lodash/map";
import set from "lodash/set";
import queryString from "query-string";
import * as React from "react";
import domToImage from "retina-dom-to-image";
import { Theme } from "../../schema/Theme";
import * as components from "../components";
import connect from "../components/connect";
import GlobalStyles from "../components/GlobalStyles";
import Select from "../components/Select";
import VariableSelector from "../components/VariableSelector";
import { themesQuery } from "../queries";
import * as templates from "../templates";

interface AssetDesignerContainerProps {
  width: WidthProperty<number | string>;
}

const AssetDesignerContainer = styled.article`
  background: #ddd;
  display: grid;
  grid-template-columns: ${({ width }) => width} 1fr;

  @media print {
    display: auto;
    grid-template-columns: auto;
  }
` as React.FC<AssetDesignerContainerProps>;

interface SidebarProps {
  backgroundColor: Color;
}

const Sidebar = styled.aside`
  padding: 1em;
  vertical-align: top;
  height: 100vh;
  position: sticky;
  background-color: ${({ backgroundColor }) => backgroundColor};

  @media print {
    display: none;
  }
` as React.FC<SidebarProps>;
const SidebarHeader = styled.h2``;
const SidebarItem = styled.div`
  margin-bottom: 1em;
`;

const Main = styled.main`
  overflow: auto;
  margin: auto;
  align-self: center;
`;

const ExportButton = styled.button``;

const VariableContainer = styled.div``;

// TODO: Share the type from the backend
interface DesignerState {
  themeId: Theme["id"];
  selectionId: string; // One of templates
  showSidebar: boolean;
  variables: { [key: string]: any };
}

enum ActionTypes {
  UPDATE_SELECTION_ID,
  UPDATE_THEME_ID,
  UPDATE_VARIABLE,
  TOGGLE_SIDEBAR,
}

function assetDesignerReducer(state: DesignerState, action) {
  const { field, value } = action;

  switch (action.type) {
    case ActionTypes.UPDATE_SELECTION_ID:
      updateQuery("selectionId", value);

      return {
        ...state,
        selectionId: value,
        variables: initializeVariables(value),
      };
    case ActionTypes.UPDATE_THEME_ID:
      updateQuery("themeId", value);

      return { ...state, themeId: value };
    case ActionTypes.UPDATE_VARIABLE:
      const newVariables = { ...state.variables };

      // Needed to support nested access (mutates!)
      set(newVariables, field, value);

      updateQuery("variables", JSON.stringify(newVariables));

      return { ...state, variables: newVariables };
    case ActionTypes.TOGGLE_SIDEBAR:
      const showSidebar = !state.showSidebar;

      updateQuery("showSidebar", showSidebar);

      return { ...state, showSidebar };
    default:
      throw new Error("No matching reducer found!");
  }
}

function updateQuery(field: string, value: any) {
  const history = createHistory();
  const query = queryString.stringify({
    ...queryString.parse(location.search),
    [field]: value,
  });
  history.push(`?${query}`);
}

interface AssetDesignerPageProps {
  initialState: {
    selectionId: DesignerState["selectionId"];
    showSidebar: boolean;
  };
  themes: Theme[];
}

function AssetDesignerPage({
  initialState = {
    selectionId: "",
    showSidebar: true,
  },
  themes,
}: AssetDesignerPageProps) {
  if (!themes) {
    return null;
  }

  // TODO: How to make sure these won't capture from inputs?
  React.useEffect(() => {
    window.addEventListener("keydown", handleUserKeyPress);

    return () => {
      window.removeEventListener("keydown", handleUserKeyPress);
    };
  }, [handleUserKeyPress]);

  function handleUserKeyPress({ key }) {
    if (key === "s") {
      dispatch({ type: ActionTypes.TOGGLE_SIDEBAR });
    }
  }

  const [state, dispatch] = React.useReducer(
    assetDesignerReducer,
    initialState,
    ({ selectionId }) => ({
      selectionId,
      showSidebar: initializeShowSidebar(),
      themeId: initializeThemeId(),
      variables: initializeVariables(selectionId),
    })
  );
  const theme = themes.find(({ id }) => id === state.themeId) || themes[0];
  const { selectionId, showSidebar } = state;

  const selection = getSelection(selectionId) || NoSelectionFound;
  const sideBarWidth = showSidebar ? "18em" : 0;
  const assetDesignTemplateId = "asset-design-template-id";

  return (
    <AssetDesignerContainer width={sideBarWidth}>
      <GlobalStyles fonts={theme.fonts} />
      {showSidebar && (
        <AssetDesignerSidebar
          themes={themes}
          theme={theme}
          assetDesignTemplateId={assetDesignTemplateId}
          selection={selection}
          selectionId={selectionId}
          variables={state.variables}
          onUpdateTheme={(field, value) =>
            dispatch({ type: ActionTypes.UPDATE_THEME_ID, field, value })
          }
          onUpdateSelection={(value) =>
            dispatch({ type: ActionTypes.UPDATE_SELECTION_ID, value })
          }
          onUpdateVariable={(field, value) =>
            dispatch({ type: ActionTypes.UPDATE_VARIABLE, field, value })
          }
        />
      )}
      <Main>
        {React.createElement(selection, {
          ...state.variables,
          theme,
          sideBarWidth,
          id: assetDesignTemplateId,
        })}
      </Main>
    </AssetDesignerContainer>
  );
}

function initializeShowSidebar() {
  return Boolean(get(queryString.parse(location.search), "showSidebar", true));
}

function initializeThemeId() {
  return get(queryString.parse(location.search), "themeId", `""`);
}

function initializeVariables(selectionId) {
  const selection = getSelection(selectionId) || {};
  const queryParameters = get(
    queryString.parse(location.search),
    "variables",
    "{}"
  );
  let queryVariables = {};

  try {
    // TODO: Figure out how to eliminate "as"
    queryVariables = JSON.parse(queryParameters as string);
  } catch (err) {
    console.log(err);
  }

  return {
    ...fromPairs(
      map(selection.variables, ({ id, validation }) => {
        return [id, get(validation, "default")];
      })
    ),
    ...queryVariables,
  };
}

function getSelection(selectionId) {
  return templates[selectionId] || components[selectionId];
}

function NoSelectionFound() {
  return <>No selection found!</>;
}

interface AssetDesignerSidebarProps {
  themes: Theme[];
  theme: Theme;
  assetDesignTemplateId: string;
  selection: any; // TODO: React component with meta
  selectionId: DesignerState["selectionId"];
  variables: DesignerState["variables"];
  onUpdateTheme: (field, value) => void;
  onUpdateSelection: (value) => void;
  onUpdateVariable: (field, value) => void;
}

function AssetDesignerSidebar({
  themes,
  theme,
  assetDesignTemplateId,
  selection,
  selectionId,
  variables,
  onUpdateTheme,
  onUpdateSelection,
  onUpdateVariable,
}: AssetDesignerSidebarProps) {
  const selectionVariables = selection.variables;
  const variableOptions = selectionVariables
    ? map(selectionVariables, (variable) => ({
        ...variable,
        value: variables[variable.id],
      }))
    : []; // TODO: Overlay to selection

  return (
    <Sidebar backgroundColor={theme ? theme.colors.background : ""}>
      <SidebarHeader>Asset designer</SidebarHeader>

      <SidebarItem>
        <ExportButton
          onClick={() => {
            const domNode = document.getElementById(assetDesignTemplateId);

            if (domNode) {
              domToImage
                .toBlob(domNode)
                .then((blob) => {
                  // TODO: Improve this further (i.e. name of the speaker for tweets etc.)
                  saveAs(blob, `${selection.filename}.png`);
                })
                .catch((err) => console.error(err));
            }
          }}
        >
          Export image as PNG
        </ExportButton>
      </SidebarItem>

      <SidebarItem>
        <ExportButton
          onClick={() => {
            const domNode = document.getElementById(assetDesignTemplateId);

            if (domNode) {
              domToImage
                .toSvg(domNode)
                .then((svg) => {
                  // TODO: Improve this further (i.e. name of the speaker for tweets etc.)
                  saveAs(svg, `${selection.filename}.svg`);
                })
                .catch((err) => console.error(err));
            }
          }}
        >
          Export image as SVG
        </ExportButton>
      </SidebarItem>

      <SidebarItem>
        <SidebarHeader>Themes</SidebarHeader>
        <ThemeSelector
          themes={themes}
          selectedTheme={theme.id}
          onChange={onUpdateTheme}
        />
      </SidebarItem>

      <SidebarItem>
        <SidebarHeader>Templates</SidebarHeader>
        <ComponentSelector
          templates={Object.keys(templates)}
          selectedTemplate={selectionId}
          onChange={onUpdateSelection}
        />
      </SidebarItem>

      <SidebarItem>
        <SidebarHeader>Components</SidebarHeader>
        <ComponentSelector
          templates={Object.keys(components)}
          selectedTemplate={selectionId}
          onChange={onUpdateSelection}
        />
      </SidebarItem>

      {variableOptions.length > 0 && (
        <SidebarItem>
          <SidebarHeader>Variables</SidebarHeader>

          {map(variableOptions, (variable) => (
            <VariableContainer key={variable.id}>
              <VariableSelector
                variables={variables}
                field={variable.id}
                selectedVariable={get(variable, "value")}
                query={variable.query}
                mapToCollection={variable.mapToCollection}
                mapToOption={variable.mapToOption}
                validation={variable.validation}
                onChange={onUpdateVariable}
              />
            </VariableContainer>
          ))}
        </SidebarItem>
      )}
    </Sidebar>
  );
}

interface ThemeSelectorProps {
  themes: Theme[];
  selectedTheme: Theme["id"];
  onChange: (field: string, value: string) => void;
}

function ThemeSelector({
  themes,
  selectedTheme,
  onChange,
}: ThemeSelectorProps) {
  return (
    <Select
      options={
        themes
          ? map(themes, (theme) => ({
              value: theme.id,
              label: theme.id,
            }))
          : []
      }
      selected={selectedTheme}
      onChange={({ target: { value } }) => {
        onChange("conferenceSeriesId", value);
      }}
    />
  );
}

interface ComponentSelectorProps {
  templates: string[];
  selectedTemplate: string;
  onChange: (value: string) => void;
}

const ComponentSelectorContainer = styled.div``;
const ComponentSelectorSelectedOption = styled.div``;
const ComponentSelectorOption = styled.a`
  display: block;
`;

function ComponentSelector({
  templates,
  selectedTemplate,
  onChange,
}: ComponentSelectorProps) {
  return (
    <ComponentSelectorContainer>
      {map(templates, (templateId) =>
        templateId === selectedTemplate ? (
          <ComponentSelectorSelectedOption key={templateId}>
            {templateId}
          </ComponentSelectorSelectedOption>
        ) : (
          <ComponentSelectorOption
            key={templateId}
            href="#"
            onClick={(e) => {
              e.preventDefault();

              onChange(templateId);
            }}
          >
            {templateId}
          </ComponentSelectorOption>
        )
      )}
    </ComponentSelectorContainer>
  );
}

const ConnectedAssetDesignerPage = connect(
  "/graphql",
  themesQuery
)(AssetDesignerPage);

export default ConnectedAssetDesignerPage;
