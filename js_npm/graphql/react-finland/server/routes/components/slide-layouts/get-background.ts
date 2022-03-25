import { Image } from "../../../schema/Image";

function getBackground(background: Image) {
  return background ? `${linearGradient()},url(${background.url})` : "";
}

// TODO: Make this more flexible
function linearGradient() {
  return `linear-gradient(rgba(0, 0, 0, 0.5), rgba(65, 35, 0, 0.05))`;
}

export default getBackground;
