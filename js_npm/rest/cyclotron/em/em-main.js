(async ()=>{
    const AppController = require("./app-driver");
    const em = require("evomaster-client-js");
    const app = new AppController();
    const controller = new em.EMController(app);

    const port = process.env.EM_PORT || 40100;
    controller.setPort(port);
    controller.startTheControllerServer();
})();