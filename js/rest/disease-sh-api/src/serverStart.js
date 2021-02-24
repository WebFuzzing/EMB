const logger = require('./utils/logger');
const app = require('./server')

const config = require('../src/config/index')

const port = config.port
app.listen(port, () => logger.info(`Your app is listening on port ${port}`));
