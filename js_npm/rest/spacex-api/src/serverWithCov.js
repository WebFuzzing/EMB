const http = require('http');
const mongoose = require('mongoose');
const { logger } = require('./middleware/logger');
const app = require('./app');

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
SERVER.listen(PORT, '127.0.0.1', () => {
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

const TB = process.env.TB || 5; // min
setTimeout(() => {
  console.log(`app closed after ${TB} min`);
  gracefulShutdown();
}, TB * 60 * 1000);
