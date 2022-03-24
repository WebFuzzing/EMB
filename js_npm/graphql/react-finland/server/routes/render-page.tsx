import trimEnd from "lodash/trimEnd";
import process from "process";
import { renderToString } from "react-dom/server";

function renderPage(title: string, baseUrl: string, page: JSX.Element) {
  return renderMarkup(title, renderToString(page), baseUrl);
}

function renderMarkup(title: string, html: string, hostname: string): string {
  const env =
    process.env.NODE_ENV === "production" ? "production.min" : "development";

  return `<!DOCTYPE html>
  <html>
    <head>
      <title>${title}</title>
      <meta charset="utf-8" />
      <script crossorigin src="https://unpkg.com/react@16/umd/react.${env}.js"></script>
      <script crossorigin src="https://unpkg.com/react-dom@16/umd/react-dom.${env}.js"></script>
      <base href="${cleanBase(hostname)}/" />
      ${reloadPage()}
    </head>
    <body>
      <div id="app">${html}</div>
    </body>
  </html>`;
}

function cleanBase(hostname: string): string {
  return trimEnd(hostname.split("?")[0], "/");
}

function reloadPage(): string {
  if (process.env.NODE_ENV === "production") {
    return "";
  }

  return `
    <script>
      let previousVersion;

      setInterval(() => {
        fetch('/ping').then(response => response.json())
        .then(({ serverVersion }) => {
          if (previousVersion) {
            if (previousVersion !== serverVersion) {
              location.reload();
            }
          }
          else {
            previousVersion = serverVersion;
          }
        }).catch(err => {
          // It's fine.
        })
      }, 500);
    </script>
  `;
}

export default renderPage;
