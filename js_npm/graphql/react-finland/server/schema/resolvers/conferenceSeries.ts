import { Theme } from "../Theme";

const conferenceSeries: {
  [key: string]: {
    id: string;
    name: string;
    conferences: string[];
    theme: Theme;
  };
} = {
  "freezing-edge": {
    id: "freezing-edge",
    name: "Freezing Edge",
    conferences: ["freezing-edge-2020"],
    theme: {
      id: "freezing-edge",
      textures: [{ url: "/media/assets/freezing-edge-bg.jpg" }],
      colors: {
        primary: "#6efbfd",
        secondary: "#3b99f7",
        text: "#000000",
        background: "#ffffff",
      },
      fonts: {
        primary: "Finlandica",
        secondary: "Work Sans",
        variants: [
          {
            family: "Finlandica",
            weight: "normal",
            style: "normal",
            fileName: "/media/fonts/finlandica-regular",
            formats: ["woff2", "woff", "ttf"],
          },
          {
            family: "Work Sans",
            weight: "normal",
            style: "normal",
            fileName: "/media/fonts/worksans-regular",
            formats: ["woff2"],
          },
        ],
      },
      logos: {
        black: {
          withText: {
            url: "/media/freezing-edge/black-logo-with-text.svg",
          },
          withoutText: {
            url: "/media/freezing-edge/black-logo.svg",
          },
        },
        colored: {
          withText: {
            url: "/media/freezing-edge/color-logo-with-text.svg",
          },
          withoutText: {
            url: "/media/freezing-edge/color-logo.svg",
          },
        },
        white: {
          withText: {
            url: "/media/freezing-edge/white-logo-with-text.svg",
          },
          withoutText: {
            url: "/media/freezing-edge/white-logo.svg",
          },
        },
      },
    },
  },
  techmovienight: {
    id: "techmovienight",
    name: "Tech Movie Night",
    conferences: ["techmovienight"],
    theme: {
      id: "techmovienight",
      textures: [
        {
          url: "/media/assets/wave.svg",
          style: {
            backgroundSize: "100% 100%, 150rem 150rem",
          },
        },
      ],
      colors: {
        primary: "#141464",
        secondary: "#272846",
        text: "#000000",
        background: "#ffffff",
      },
      fonts: {
        primary: "Finlandica",
        secondary: "Work Sans",
        variants: [
          {
            family: "Finlandica",
            weight: "normal",
            style: "normal",
            fileName: "/media/fonts/finlandica-regular",
            formats: ["woff2", "woff", "ttf"],
          },
          {
            family: "Work Sans",
            weight: "normal",
            style: "normal",
            fileName: "/media/fonts/worksans-regular",
            formats: ["woff2"],
          },
        ],
      },
      logos: {
        black: {
          withText: {
            url: "/media/techmovienight/black-logo-with-text.svg",
          },
          withoutText: {
            url: "/media/techmovienight/black-logo.svg",
          },
        },
        colored: {
          withText: {
            url: "/media/techmovienight/black-logo-with-text.svg",
          },
          withoutText: {
            url: "/media/techmovienight/black-logo.svg",
          },
        },
        white: {
          withText: {
            url: "/media/techmovienight/white-logo-with-text.svg",
          },
          withoutText: {
            url: "/media/techmovienight/white-logo.svg",
          },
        },
      },
    },
  },
  halfstack: {
    id: "halfstack",
    name: "HalfStack",
    conferences: ["halfstack-vienna-2019"],
    theme: {
      id: "halfstack",
      textures: [{ url: "/media/halfstack/texture.svg" }],
      colors: {
        primary: "#D62329",
        secondary: "#ffffff",
        text: "#000000",
        background: "#ffffff",
      },
      // TODO: Figure out which font to use
      fonts: {
        primary: "Nunito",
        secondary: "Nunito",
        variants: [
          {
            href: "https://fonts.googleapis.com/css?family=Nunito",
          },
        ],
      },
      logos: {
        // TODO: A black version of the logo
        black: {
          withText: {
            url: "/media/sponsors/halfstack.svg",
          },
          withoutText: {
            url: "/media/sponsors/halfstack.svg",
          },
        },
        colored: {
          withText: {
            url: "/media/sponsors/halfstack.svg",
          },
          // TODO: A compact version without text
          withoutText: {
            url: "/media/sponsors/halfstack.svg",
          },
        },
        // TODO: A white version of the logo
        white: {
          withText: {
            url: "/media/sponsors/halfstack.svg",
          },
          withoutText: {
            url: "/media/sponsors/halfstack.svg",
          },
        },
      },
    },
  },
  typeof: {
    id: "typeof",
    name: "typeof",
    conferences: ["typeof-2019"],
    theme: {
      id: "typeof",
      textures: [{ url: "/media/assets/typeof.png" }],
      colors: {
        primary: "#9013fe",
        secondary: "#2d2a96",
        text: "#09020f",
        background: "#ffffff",
      },
      fonts: {
        primary: "Nunito",
        secondary: "Nunito",
        variants: [
          {
            href: "https://fonts.googleapis.com/css?family=Nunito",
          },
        ],
      },
      logos: {
        black: {
          withText: {
            url: "/media/typeof/logo/logo-black-with-text.svg",
          },
          withoutText: {
            url: "/media/typeof/logo/logo-black.svg",
          },
        },
        colored: {
          withText: {
            url: "/media/typeof/logo/logo-colored-with-text.svg",
          },
          withoutText: {
            url: "/media/typeof/logo/logo-colored.svg",
          },
        },
        white: {
          withText: {
            url: "/media/typeof/logo/logo-white-with-text.svg",
          },
          withoutText: {
            url: "/media/typeof/logo/logo-white.svg",
          },
        },
      },
    },
  },
  "reason-conf": {
    id: "reason-conf",
    name: "ReasonConf",
    conferences: ["reason-conf-2019"],
    theme: {
      id: "reason-conf",
      textures: [{ url: "/media/reason-conf/texture.png" }],
      colors: {
        primary: "#dd4b39",
        secondary: "#385571",
        text: "#fdfdfd",
        background: "#F6F4F1",
      },
      fonts: {
        primary: "Montserrat",
        secondary: "Work Sans",
        variants: [
          {
            href: "https://fonts.googleapis.com/css?family=Montserrat",
          },
          {
            href: "https://fonts.googleapis.com/css?family=Work+Sans",
          },
        ],
      },
      logos: {
        black: {
          withText: {
            url: "/media/reason-conf/logo/logo-black-with-text.svg",
          },
          withoutText: {
            url: "/media/reason-conf/logo/logo-black.svg",
          },
        },
        colored: {
          withText: {
            url: "/media/reason-conf/logo/logo-colored-with-text.svg",
          },
          withoutText: {
            url: "/media/reason-conf/logo/logo-colored.svg",
          },
        },
        white: {
          withText: {
            url: "/media/reason-conf/logo/logo-white-with-text.svg",
          },
          withoutText: {
            url: "/media/reason-conf/logo/logo-white.svg",
          },
        },
      },
    },
  },
  "react-finland": {
    id: "react-finland",
    name: "React Finland",
    conferences: [
      "react-finland-2018",
      "react-finland-2019",
      "react-finland-2020",
      "react-finland-2021",
      "react-finland-2022",
      "rf-2020-01",
    ],
    theme: {
      id: "react-finland",
      textures: [{ url: "/media/assets/wave.svg" }],
      colors: {
        primary: "#002fa9",
        secondary: "#5297e9",
        text: "#fdfdfd",
        background: "#eee",
      },
      fonts: {
        primary: "Finlandica",
        secondary: "Work Sans",
        variants: [
          {
            family: "Finlandica",
            weight: "normal",
            style: "normal",
            fileName: "/media/fonts/finlandica-regular",
            formats: ["woff2", "woff", "ttf"],
          },
          {
            family: "Work Sans",
            weight: "normal",
            style: "normal",
            fileName: "/media/fonts/worksans-regular",
            formats: ["ttf"],
          },
        ],
      },
      logos: {
        black: {
          withText: {
            url: "/media/react-finland/logo/v2/logo-black-with-text.svg",
          },
          withoutText: {
            url: "/media/react-finland/logo/v2/logo-black.svg",
          },
        },
        colored: {
          withText: {
            url: "/media/react-finland/logo/v2/logo-colored-with-text.svg",
          },
          withoutText: {
            url: "/media/react-finland/logo/v2/logo-colored.svg",
          },
        },
        white: {
          withText: {
            url: "/media/react-finland/logo/v2/logo-white-with-text.svg",
          },
          withoutText: {
            url: "/media/react-finland/logo/v2/logo-white.svg",
          },
        },
      },
    },
  },
  "graphql-finland": {
    id: "graphql-finland",
    name: "GraphQL Finland",
    conferences: ["graphql-finland-2018", "graphql-finland-2020"],
    theme: {
      id: "graphql-finland",
      textures: [{ url: "/media/assets/wave.svg" }],
      colors: {
        primary: "#e10098",
        secondary: "#ff1eb6",
        text: "#233239",
        background: "#eee",
      },
      fonts: {
        primary: "Finlandica",
        secondary: "Work Sans",
        variants: [
          {
            family: "Finlandica",
            weight: "normal",
            style: "normal",
            fileName: "/media/fonts/finlandica-regular",
            formats: ["woff2", "woff", "ttf"],
          },
          {
            family: "Work Sans",
            weight: "normal",
            style: "normal",
            fileName: "/media/fonts/worksans-regular",
            formats: ["woff2"],
          },
        ],
      },
      logos: {
        black: {
          withText: {
            url: "/media/graphql-finland/logo/v1/logo-black-with-text.svg",
          },
          withoutText: {
            url: "/media/graphql-finland/logo/v1/logo-black.svg",
          },
        },
        colored: {
          withText: {
            url: "/media/graphql-finland/logo/v1/logo-colored-with-text.svg",
          },
          withoutText: {
            url: "/media/graphql-finland/logo/v1/logo-colored.svg",
          },
        },
        white: {
          withText: {
            url: "/media/graphql-finland/logo/v1/logo-white-with-text.svg",
          },
          withoutText: {
            url: "/media/graphql-finland/logo/v1/logo-white.svg",
          },
        },
      },
    },
  },
};

export default conferenceSeries;
