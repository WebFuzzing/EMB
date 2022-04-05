const { GenericContainer } = require("testcontainers");

//https://www.npmjs.com/package/postgres
//const postgres = require("postgres");
//https://node-postgres.com/
const postgres = require("pg");

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

        connection = new postgres.Client({
            user: 'foo',
            host: 'localhost',
            database: 'db',
            password: 'bar',
            port: process.env.DB_PORT,
        });

        await connection.connect();

        // connection = postgres(dbURL, {
        //   //  host                 : '',            // Postgres ip address[s] or domain name[s]
        //   //  port                 : 5432,          // Postgres server port[s]
        //     database             : 'db',            // Name of database to connect to
        //     username             : 'foo',            // Username of database user
        //     password             : 'bar',            // Password of database user
        // })

        return test_container;

    },

    checkdb: async () =>{
        console.log("todo check")
    },

    cleanDb: () =>{

        const queryAllTables = 'SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES where (TABLE_TYPE=\'TABLE\' OR TABLE_TYPE=\'BASE TABLE\')'
                + " AND TABLE_SCHEMA='public' ";

        return new Promise(resolve => {

//            connection([queryAllTables])
            connection.query(queryAllTables)
                    .then(results => {
                    let truncateAll= 'TRUNCATE TABLE ';
                    let resetSeq = '';
                    truncateAll += results.rows.map(_ => _.table_name).join(",")
                    // for (const element of results.rows){
                    //     truncateAll += `${element.table_name},`;
                    //     //TODO
                    //     //resetSeq += `ALTER SEQUENCE ${element.TABLE_NAME} RESTART WITH 1;`;
                    // }
                    truncateAll += ";"

                    //connection([truncateAll+resetSeq]).then(f=>resolve())
                    connection.query(truncateAll+resetSeq).then(f=>resolve())
                })
        })
    },

    stopDb : async () =>{
        if (connection)
            await connection.end();

        if (test_container){
            await test_container.stop();
            test_container = null;
        }
    }
}