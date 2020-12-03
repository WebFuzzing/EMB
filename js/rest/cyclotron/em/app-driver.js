const http  = require("http");
const {AddressInfo}  = require("net");
const mongoose = require('mongoose');
const app = require("../src/app");

const em = require("evomaster-client-js");


/*
    https://github.com/KristianWEB/fakebooker-backend/blob/471d6f6fafc95af57a99b6506c8f945dce43ffe9/jest.setup.js
    https://kb.objectrocket.com/postgresql/mongoose-drop-collection-if-exists-605
    collections are created by startSUT,
    before each of tests, we only clean documents for all collections.
 */
const clean = async () => {
    for (const key of Object.keys(mongoose.connection.collections)) {
        await mongoose.connection.collections[key].deleteMany({});
        //await mongoose.connection.db.dropCollection(key);
    }
}


class AppController  extends em.SutController {

    constructor(test_container) {
        super();
        this.test_container= test_container;
    }

    setupForGeneratedTest(){
        return Promise.resolve();
    }

    getInfoForAuthentication(){
        return [];
    }

    getPreferredOutputFormat() {
        return em.dto.OutputFormat.JS_JEST;
    }

    getProblemInfo() {
        const dto = new em.dto.RestProblemDto();
        dto.swaggerJsonUrl = "http://localhost:" + this.port + "/swagger.json";

        return dto;
    }

    isSutRunning(){
        if (!this.server) {
            return false;
        }
        return this.server.listening;
    }

    resetStateOfSUT(){
        clean();
        return Promise.resolve();

    }

    startSut(){
        clean();
        //docker run -p 27017:27017 mongo
        return new Promise( (resolve) => {
            this.server = app.listen(0, "localhost", () => {
                this.port = this.server.address().port;
                resolve("http://localhost:" + this.port);
            });
        });
    }

    stopSut() {
        return new Promise( (resolve) =>
            {
                this.server.close( () => resolve());
                // https://mongoosejs.com/docs/api/connection.html#connection_Connection-readyState
                mongoose.connection.close();
                this.test_container.stop();
            }
        );
    }

}


module.exports = AppController;