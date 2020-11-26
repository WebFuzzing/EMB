const logger = require('./utils/logger');
const app = require('./server')

app.listen(port, () => logger.info(`Your app is listening on port ${port}`));
