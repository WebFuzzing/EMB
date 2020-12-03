const { GenericContainer } = require("testcontainers");

(async ()=>{

   DB_PORT = process.env.DB_PORT || 27017;
   const test_container= await new GenericContainer("mongo", "3.5")
       .withExposedPorts(DB_PORT)
       .start();

   process.env.DB_URL = `mongodb://localhost:${test_container.getMappedPort(DB_PORT)}/cyclotron`;
   //console.log(require("../src/config/config").mongodb);

   const AppController = require("./app-driver");
   const em = require("evomaster-client-js");
   const controller = new em.EMController(new AppController());

   port = process.env.EM_PORT || 40100;
   controller.setPort(port);
   controller.startTheControllerServer();
})();