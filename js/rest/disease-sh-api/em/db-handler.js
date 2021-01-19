const { DockerComposeEnvironment , Wait } = require("testcontainers");

let dbPort = 27017;
let exposedDbPort = 0;
let dbURL = ""
let test_container = null;

module.exports ={
    startDb: async () =>{
        console.log("start db")
        dbPort = process.env.DB_PORT || 50000;

        const environment = await new DockerComposeEnvironment(__dirname, "test-redis-db.yml")
        .withWaitStrategy("redis_1", Wait.forLogMessage("Ready to accept connections"))
        .up();
        test_container = await environment.getContainer("redis_1");
        exposedDbPort = test_container.getMappedPort(dbPort);
        process.env.REDIS_PORT = exposedDbPort;

        console.log("connecting redis-server with "+exposedDbPort);
        return test_container;
    },

    checkdb: async () =>{

    },

    getDbPort: () =>{
        return exposedDbPort
    },

    cleanDb: async () =>{

    },

    stopDb : () =>{
        if (test_container){
            test_container.stop();
            test_container = null;
        }
    }


}