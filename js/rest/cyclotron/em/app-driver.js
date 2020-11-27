const http  = require("http");
const {AddressInfo}  = require("net");
const mongoose = require('mongoose');

const app = require("../src/app");

const em = require("evomaster-client-js");


class AppController  extends em.SutController {

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
        // mongoose.connection.db.dropDatabase();
        return Promise.resolve();

    }

    startSut(){

        //for mogodb
        // mongoose.connection.close();
        // mongoose.connection.db.dropDatabase();
        //
        //docker run -p 27017:27017 mongo
        console.log("mongoose connection: "+mongoose.connection.readyState)
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
            }
        );
    }

}


module.exports = AppController;