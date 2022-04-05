const AppController = require("./app-driver");
const em = require("evomaster-client-js");


const controller = new em.EMController(new AppController());


port = process.env.EM_PORT || 40100;
controller.setPort(port);

controller.startTheControllerServer();


