const AppController = require("./app-driver");
const em = require("evomaster-client-js");
const controller = new em.EMController(new AppController());

port = process.env.EM_PORT || 40100;
controller.setPort(port);
controller.startTheControllerServer();

// Man: following code is to handle db with test container, but it is extremely slow.
// const { DockerComposeEnvironment , Wait } = require("testcontainers");
//
// (async ()=>{
//
//     const environment = await new DockerComposeEnvironment(__dirname, "test-redis-db.yml")
//         .withWaitStrategy("redis_1", Wait.forLogMessage("Ready to accept connections"))
//         .up();
//
//     const container = await environment.getContainer("redis_1");
//     process.env.REDIS_PORT = container.getMappedPort(50000);
//
//     console.log(process.env.REDIS_PORT);
//     const AppController = require("./app-driver");
//     const em = require("evomaster-client-js");
//     const controller = new em.EMController(new AppController());
//
//     port = process.env.EM_PORT || 40100;
//     controller.setPort(port);
//     controller.startTheControllerServer();
// })();
