const AppController = require("./app-driver");
const em = require("evomaster-client-js");


const controller = new em.EMController(new AppController());

controller.setPort(config.port);

controller.startTheControllerServer();


