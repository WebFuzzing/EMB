function routePing(router) {
  if (process.env.NODE_ENV !== "production") {
    const serverVersion = new Date().toString();

    router.get("/ping", (req, res) => res.status(200).json({ serverVersion }));
  }
}

export default routePing;
