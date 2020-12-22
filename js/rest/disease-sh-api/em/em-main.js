/*
    enable test container with redis as default, but it may take time.
    NOTE THAT you can also directly connect with the local redis with two ways, i.e.,
    1) the command
        docker run -d -v <absolute path to dump.rdb>:/data/dump.rdb -p 6379:6379 redis
    2) docker-compose, i.e., test-redis-db.yml
 */
const { DockerComposeEnvironment , Wait } = require("testcontainers");

(async ()=>{

    const environment = await new DockerComposeEnvironment(__dirname, "test-redis-db.yml")
        .withWaitStrategy("redis_1", Wait.forLogMessage("Ready to accept connections"))
        .up();

    const container = await environment.getContainer("redis_1");
    process.env.REDIS_PORT = container.getMappedPort(50000);

    console.log(process.env.REDIS_PORT);
    const AppController = require("./app-driver");
    const em = require("evomaster-client-js");
    const controller = new em.EMController(new AppController());

    port = process.env.EM_PORT || 40100;
    controller.setPort(port);
    controller.startTheControllerServer();
})();

// const AppController = require("./app-driver");
// const em = require("evomaster-client-js");
// const controller = new em.EMController(new AppController());
//
// port = process.env.EM_PORT || 40100;
// controller.setPort(port);
// controller.startTheControllerServer();
