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
        const dbURL = `mongodb://localhost:${exposedDbPort}/spacex`
        process.env.SPACEX_MONGO = dbURL;

        console.log("connecting "+dbURL);
        return test_container;
    },

    checkdb: async () =>{
        if (mongoose.connection.readyState != 1)
            setTimeout(checkdb, 300)
    },

    initAuth: async (key)=>{
        await mongoose.connection.useDb('auth')
            .collection('users')
            .insertOne({
                key: key,
                //TODO some roles are missing here, eg on capsule
                roles: [
                    'cache:clear',
                    'company:update',
                    'core:create', 'core:update', 'core:delete',
                    'crew:create', 'crew:update', 'crew:delete',
                    'dragon:create', 'dragon:update', 'dragon:delete',
                    'fairing:create', 'fairing:update', 'fairing:delete',
                    'history:create', 'history:update', 'history:delete',
                    'landpad:create', 'landpad:update', 'landpad:delete',
                    'launch:create', 'launch:update', 'launch:delete',
                    'launchpad:create', 'launchpad:update', 'launchpad:delete',
                    'payload:create', 'payload:update', 'payload:delete',
                    'roadster:update',
                    'rocket:create', 'rocket:update', 'rocket:delete',
                    'ship:create', 'ship:update', 'ship:delete',
                    'starlink:create', 'starlink:update', 'starlink:delete',
                    'user:create', 'user:update', 'user:delete'
                ]});
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

        // redis
        const cache = require("../src/middleware/cache")
        if(cache && cache.redis && cache.redis.status == "ready"){
            await cache.redis.flushall();
        }
    },

    stopDb : async () =>{
        if (test_container){
            await mongoose.connection.close();

            const cache = require("../src/middleware/cache")
            if(cache && cache.redis && cache.redis.status == "ready"){
                await cache.redis.disconnect();
            }

            await test_container.stop();
            test_container = null;
        }
    }


}