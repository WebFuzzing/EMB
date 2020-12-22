const logger = require('./utils/logger');
const app = require('./server')

const config = require('../src/config/index')

const port = config.port
const server = app.listen(port, () => logger.info(`Your app is listening on port ${port}`));


const TB = process.env.TB || 5 //min
setTimeout(function () {
    console.log("stopped by timeout")
    server.close(() => {
        console.log(`app closed after ${TB} min`);
        process.exit(0);
    });},  TB * 60  * 1000)
