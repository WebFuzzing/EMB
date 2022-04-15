const http = require('http');
const mongoose = require('mongoose');
const { logger } = require('./middleware/logger');
const app = require('./app');
const Router = require("koa-router");


const router = new Router();
/*
    Added to be able to collect coverage with C8. See:
    https://github.com/bcoe/c8/issues/166
 */
//setTimeout(()=> process.exit(0), 10000)
// process.on("SIGINT", () =>{console.log("SIGINT"); process.exit(0)})
// process.on("SIGTERM", () =>{console.log("SIGTERM"); process.exit(0)})
// process.on("SIGUSR1", () =>{console.log("SIGUSR1"); process.exit(0)})
router.post("/shutdown", () => process.exit(0))
app.use(router.routes());

const PORT = process.env.PORT || 6673;
const SERVER = http.createServer(app.callback());

// Gracefully close Mongo connection
const gracefulShutdown = () => {
  mongoose.connection.close(false, () => {
    logger.info('Mongo closed');
    SERVER.close(() => {
      logger.info('Shutting down...');
      process.exit();
    });
  });
};

// Server start
SERVER.listen(PORT, '0.0.0.0', () => {
  logger.info(`Running on port: ${PORT}`);

  // // Handle kill commands
  // process.on('SIGTERM', gracefulShutdown);
  //
  // // Prevent dirty exit on code-fault crashes:
  // process.on('uncaughtException', gracefulShutdown);
  //
  // // Prevent promise rejection exits
  // process.on('unhandledRejection', gracefulShutdown);
});


