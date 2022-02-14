const { GenericContainer } = require("testcontainers");
const mysql = require('mysql');

//docker run --name mysql_db -e MYSQL_ROOT_PASSWORD=test -e MYSQL_USER=test -e MYSQL_PASSWORD=test  -e MYSQL_DATABASE=test -p 3306:3306 -d mysql:5.7.22

let dbPort = 3306;
let exposedDbPort = 0;
let dbURL = "mysql://localhost/test"
let test_container = null;
let connection = null;


function querySql(sql)  {

    return new Promise((resolve, reject) => {
        connection.query(sql, (err, results) => {
            if (err) {
                return reject(err);
            }
            resolve(results);
        });
    })
}

module.exports ={

    startDb: async () =>{

        dbPort = process.env.DB_PORT || 3306;

        if(process.env.DOCKER_DBC && process.env.DOCKER_DBC === '0'){
            process.env.DB_PORT = dbPort
            console.log("use local mysql on port:" + dbPort)
        }else{
            console.log("start db with test container")
            test_container= await new GenericContainer("mysql:5.7.22")
                .withEnv("MYSQL_ROOT_PASSWORD", "test")
                .withEnv("MYSQL_USER", "test")
                .withEnv("MYSQL_PASSWORD", "test")
                .withEnv("MYSQL_DATABASE", "test")
                .withExposedPorts(dbPort)
                .withTmpFs({ "/var/lib/mysql": "rw" })
                .start();
            exposedDbPort = test_container.getMappedPort(dbPort)
            process.env.DB_PORT = exposedDbPort
            dbURL = `mysql://localhost:${exposedDbPort}/test`

            console.log("connecting "+dbURL);
        }

        connection = mysql.createConnection({
            host : 'localhost',
            port : process.env.DB_PORT,
            user : 'test',
            password : 'test',
            database : 'test',
            multipleStatements: true
        });

        await connection.connect();

        return test_container;

    },

    checkdb: async () =>{
        console.log("todo check")
    },

    cleanDb: () =>{
        // https://www.npmjs.com/package/mysql

        const disableReferentialIntegrity = 'SET @@foreign_key_checks = 0';
        const queryAllTables = 'SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES where (TABLE_TYPE=\'TABLE\' OR TABLE_TYPE=\'BASE TABLE\')';
        const enableReferentialIntegrity = 'SET @@foreign_key_checks = 1';

        return new Promise(resolve => {
            querySql(disableReferentialIntegrity).then(r =>
                querySql(queryAllTables).then(results => {
                    let truncateAll= '';
                    let resetSeq = '';
                    for (const element of results){
                        truncateAll += `TRUNCATE TABLE ${element.TABLE_NAME};`;
                        resetSeq += `ALTER TABLE ${element.TABLE_NAME} AUTO_INCREMENT=1;`;
                    }

                    querySql(truncateAll+resetSeq).then(e=>
                        querySql(enableReferentialIntegrity).then(f=>resolve())
                    )
                })
            );
        })
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