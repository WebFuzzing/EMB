const {DockerComposeEnvironment, Wait} = require("testcontainers");

let test_container = null;

module.exports = {
    startDb: async () => {

        console.log("starting docker db")
        const dbPort =  50000;

        // solve ioredis connection problem https://github.com/luin/ioredis/issues/763
        const environment = await new DockerComposeEnvironment(__dirname, "test-redis-db.yml")
            .withWaitStrategy("redis_1", Wait.forLogMessage("Ready to accept connections"))
            .up();
        test_container = await environment.getContainer("redis_1");
        const exposedDbPort = test_container.getMappedPort(dbPort);
        process.env.REDIS_PORT = exposedDbPort;

        console.log("connecting redis-server with " + exposedDbPort + " " + test_container.getHost());

        return test_container;
    },

    checkdb: async () => {
    },


    cleanDb: async () => {
        //read-only DB
    },

    stopDb: () => {
        if (test_container) {
            test_container.stop();
            test_container = null;
        }
    }


}