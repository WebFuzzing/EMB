import * as fs from "fs";
import { FuseBox } from "fuse-box";
import trimStart from "lodash/trimStart";
import mkdirp from "mkdirp";
import * as path from "path";
import * as React from "react";

function createInteractive(projectRoot, scriptRoot, componentRoot) {
  mkdirp.sync(scriptRoot);

  interface IInteractiveContext {
    registerBundler: () => void;
    bundlingCompleted: (err) => void;
  }

  // TODO: Figure out how to handle this
  const InteractiveContext = React.createContext<IInteractiveContext>({
    registerBundler: () => {},
    bundlingCompleted: () => {},
  });

  function InteractiveConsumer({
    component,
    relativeComponentPath,
    componentHash = "",
    props = {},
  }) {
    const componentName = `${path.basename(
      relativeComponentPath
    )}${componentHash}`;
    const indexName = `${componentName}.index.ts`;
    const indexPath = path.join(scriptRoot, indexName);
    const absoluteComponentPath = path.join(
      componentRoot,
      relativeComponentPath
    );
    const outputPath = `${path.join(scriptRoot, componentName)}.js`;
    const scriptPath = `/${trimStart(
      path.relative(projectRoot, outputPath),
      "."
    )}`;

    fs.writeFileSync(
      indexPath,
      renderScript(absoluteComponentPath, componentName, props)
    );

    // TODO: Likely we should use Preact here (alias?)
    const fuse = FuseBox.init({
      target: "browser@es6",
      homeDir: scriptRoot,
      output: `${scriptRoot}/$name.js`,
      shim: {
        "type-graphql": {
          source: path.join(projectRoot, "shims/type-graphql.js"),
          exports: "TypeGraphQL",
        },
        react: {
          exports: "React",
        },
        "react-dom": {
          exports: "ReactDOM",
        },
      },
      // TODO: Apply terser to minify
    });

    fuse
      .bundle(componentName)
      .cache(false) // TODO: Enable cache again?
      .instructions(`> ${indexName}`);

    const { registerBundler, bundlingCompleted } = React.useContext(
      InteractiveContext
    );

    registerBundler();

    fuse
      .run()
      .then(bundlingCompleted)
      .catch(bundlingCompleted);

    // Since rendering is streaming, it's important the script gets executed
    // only after the div has been created! Otherwise hydration fails.
    return (
      <>
        <div id={componentName}>{React.createElement(component, props)}</div>
        <script src={scriptPath} />
      </>
    );
  }

  return {
    Provider: InteractiveContext.Provider,
    Consumer: InteractiveConsumer,
  };
}

function renderScript(componentPath, componentName, props = {}) {
  return `import React from "react";
import { hydrate } from "react-dom";
import component from "${componentPath}";

hydrate(
  React.createElement(component, ${JSON.stringify(props)}),
  document.getElementById("${componentName}")
);`;
}

export default createInteractive;
