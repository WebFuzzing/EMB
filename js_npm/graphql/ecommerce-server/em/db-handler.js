const { GenericContainer } = require("testcontainers");

//https://www.npmjs.com/package/postgres
const postgres = require("postgres");

// docker run -p 5432:5432 -e POSTGRES_PASSWORD=bar -e POSTGRES_USER=foo postgres:11

let dbPort = 5432;
let exposedDbPort = 0;
let dbURL = "postgres://localhost"
let test_container = null;
let connection = null;



module.exports ={

    startDb: async () =>{

        dbPort = process.env.DB_PORT || 5432;

        if(process.env.DOCKER_DBC && process.env.DOCKER_DBC === '0'){
            process.env.DB_PORT = dbPort
            console.log("use local Postgres on port:" + dbPort)
        }else{
            console.log("start db with test container")
            test_container= await new GenericContainer("postgres:11")
                .withEnv("POSTGRES_USER", "foo")
                .withEnv("POSTGRES_PASSWORD", "bar")
                .withEnv("POSTGRES_DB","db")
                .withExposedPorts(dbPort)
                .withTmpFs({ "/var/lib/postgresql/data": "rw" })
                .start();
            exposedDbPort = test_container.getMappedPort(dbPort)
            process.env.DB_PORT = exposedDbPort
            dbURL = `postgres://localhost:${exposedDbPort}`

            console.log("connecting "+dbURL);
        }

        connection = postgres(dbURL, {
          //  host                 : '',            // Postgres ip address[s] or domain name[s]
          //  port                 : 5432,          // Postgres server port[s]
            database             : 'db',            // Name of database to connect to
            username             : 'foo',            // Username of database user
            password             : 'bar',            // Password of database user
        })

        return test_container;

    },

    checkdb: async () =>{
        console.log("todo check")
    },

    cleanDb: () =>{

        const queryAllTables = 'SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES where (TABLE_TYPE=\'TABLE\' OR TABLE_TYPE=\'BASE TABLE\')'
                + " AND TABLE_SCHEMA='public' ";

        //TODO
        // return new Promise(resolve => {
        //
        //         querySql(queryAllTables)
        //             .then(results => {
        //             let truncateAll= '';
        //             let resetSeq = '';
        //             for (const element of results){
        //                 truncateAll += `TRUNCATE TABLE ${element.TABLE_NAME};`;
        //                 resetSeq += `ALTER TABLE ${element.TABLE_NAME} AUTO_INCREMENT=1;`;
        //             }
        //
        //             querySql(truncateAll+resetSeq).then(e=>
        //                 querySql(enableReferentialIntegrity).then(f=>resolve())
        //             )
        //         })

        return Promise.resolve();
    },

    stopDb : async () =>{
        if (connection)
            connection.end();

        if (test_container){
            await test_container.stop();
            test_container = null;
        }
    }
}