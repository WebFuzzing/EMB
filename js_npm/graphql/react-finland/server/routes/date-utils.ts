// TODO: Use browser here instead?
function dayToFinnishLocale(day: string): string {
  const date = new Date(day);

  return `${date.getDate()}.${date.getMonth() + 1}.${date.getFullYear()}`;
}

export { dayToFinnishLocale };
