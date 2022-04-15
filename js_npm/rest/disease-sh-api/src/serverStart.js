const logger = require('./utils/logger');
const app = require('./server')

const config = require('../src/config/index')

const port = config.port
app.listen(port, () => logger.info(`Your app is listening on port ${port}`));


/*
    Added to be able to collect coverage with C8. See:
    https://github.com/bcoe/c8/issues/166
 */
//setTimeout(()=> process.exit(0), 10000)
// process.on("SIGINT", () =>{console.log("SIGINT"); process.exit(0)})
// process.on("SIGTERM", () =>{console.log("SIGTERM"); process.exit(0)})
// process.on("SIGUSR1", () =>{console.log("SIGUSR1"); process.exit(0)})
app.post("/shutdown", () => process.exit(0))