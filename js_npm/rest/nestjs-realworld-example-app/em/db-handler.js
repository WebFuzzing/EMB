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
        console.log("start db")
        dbPort = process.env.DB_PORT || 3306;
        test_container= await new GenericContainer("mysql", "5.7.22")
            .withEnv("MYSQL_ROOT_PASSWORD", "test")
            .withEnv("MYSQL_USER", "test")
            .withEnv("MYSQL_PASSWORD", "test")
            .withEnv("MYSQL_DATABASE", "test")
            .withExposedPorts(dbPort)
            .start();
        exposedDbPort = test_container.getMappedPort(dbPort)
        process.env.DB_PORT = exposedDbPort
        dbURL = `mysql://localhost:${exposedDbPort}/test`

        console.log("connecting "+dbURL);

        connection = mysql.createConnection({
            host : 'localhost',
            port : exposedDbPort,
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

    // discuss with Andrea, whether we put this code in client-js
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


        // connection.beginTransaction( function (error) {
        //     if (error) throw error;
        //
        //     connection.query(disableReferentialIntegrity, function (error) {
        //         if (error){
        //             return connection.rollback(function(){throw error;});
        //         }
        //
        //         connection.query(queryAllTables, function (error, results){
        //             if (error) {
        //                 return connection.rollback(function() {
        //                     throw error;
        //                 });
        //             }
        //
        //             let truncateAll= '';
        //             let resetSeq = '';
        //             for (const element of results){
        //                 truncateAll += `TRUNCATE TABLE ${element.TABLE_NAME};`;
        //                 resetSeq += `ALTER TABLE ${element.TABLE_NAME} AUTO_INCREMENT=1;`;
        //             }
        //
        //             connection.query(truncateAll + resetSeq, function (error){
        //                 if (error) {
        //                     return connection.rollback(function() {
        //                         throw error;
        //                     });
        //                 }
        //
        //                 connection.query(enableReferentialIntegrity, function(error){
        //                     if (error) {
        //                         return connection.rollback(function() {
        //                             throw error;
        //                         });
        //                     }
        //
        //                     connection.commit(async function(err) {
        //                         if (err) {
        //                             return connection.rollback(function() {
        //                                 throw err;
        //                             });
        //                         }
        //                         console.log('clean db done!');
        //                     });
        //                 });
        //             });
        //         });
        //     });
        // });

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