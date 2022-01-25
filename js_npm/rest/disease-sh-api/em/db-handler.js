const { DockerComposeEnvironment , Wait } = require("testcontainers");

let dbPort = 27017;
let exposedDbPort = 0;
let dbURL = ""
let test_container = null;

module.exports ={
    startDb: async () =>{
        if(process.env.DOCKER_DBC && process.env.DOCKER_DBC === '0'){
            console.log("use local redis on port:" + process.env.REDIS_PORT)
        }else{
            console.log("start docker db")
            dbPort = process.env.DB_PORT || 50000;

            // solve ioredis connection problem https://github.com/luin/ioredis/issues/763
            const environment = await new DockerComposeEnvironment(__dirname, "test-redis-db.yml")
                .withWaitStrategy("redis_1", Wait.forLogMessage("Ready to accept connections"))
                .up();
            test_container = await environment.getContainer("redis_1");
            exposedDbPort = test_container.getMappedPort(dbPort);
            process.env.REDIS_PORT = exposedDbPort;

            console.log("connecting redis-server with " + exposedDbPort+ " " + test_container.getHost());
        }
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