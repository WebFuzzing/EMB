const { GenericContainer } = require("testcontainers");
const mongoose = require('mongoose');

let test_container = null;

module.exports ={
    startDb: async () =>{
        console.log("start db")
        const dbPort = 27017;
        test_container= await new GenericContainer("mongo", "3.5")
            .withExposedPorts(dbPort)
            .start();
        const exposedDbPort = test_container.getMappedPort(dbPort)
        const dbURL = `mongodb://localhost:${exposedDbPort}/cyclotron`
        process.env.DB_URL = dbURL;

        console.log("connecting "+dbURL);
        return test_container;
    },

    checkdb: async () =>{
        if (mongoose.connection.readyState != 1)
            setTimeout(checkdb, 300)
    },


    /*
        https://github.com/KristianWEB/fakebooker-backend/blob/471d6f6fafc95af57a99b6506c8f945dce43ffe9/jest.setup.js
        https://kb.objectrocket.com/postgresql/mongoose-drop-collection-if-exists-605
        collections are created by startSUT,
        before each of tests, we only clean documents for all collections.
     */
    cleanDb: async () =>{
        for (const key of Object.keys(mongoose.connection.collections)) {
            await mongoose.connection.collections[key].deleteMany({});
            //await mongoose.connection.db.dropCollection(key);
        }
    },

    stopDb : () =>{
        if (test_container){
            test_container.stop();
            test_container = null;
        }
    }


}