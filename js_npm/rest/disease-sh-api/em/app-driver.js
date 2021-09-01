const dbHandler = require("./db-handler");

const http  = require("http");
const {AddressInfo}  = require("net");

const em = require("evomaster-client-js");


class AppController extends em.SutController {


    setupForGeneratedTest(){

        return new Promise((resolve)=>{
            this.testcontainer = dbHandler.startDb();
            resolve(this.testcontainer);
        });
    }

    getInfoForAuthentication(){
        return [];
    }

    getPreferredOutputFormat() {
        return em.dto.OutputFormat.JS_JEST;
    }

    getProblemInfo() {
        const dto = new em.dto.RestProblemDto();
        dto.swaggerJsonUrl = "http://localhost:" + this.port + "/apidocs/swagger_v3.json";

        return dto;
    }

    isSutRunning(){
        if (!this.server) {
            return false;
        }
        return this.server.listening;
    }

    resetStateOfSUT(){
        return Promise.resolve();
    }

    startSut(){
        const app = require("../src/server");
        return new Promise( (resolve) => {
            this.port = require('../src/config/index').port;
            this.server = app.listen(this.port, "localhost", () => {
                resolve("http://localhost:" + this.port);
            });
        });
    }

    stopSut() {
        return new Promise( (resolve) => {
                this.server.close( () => {
                    dbHandler.stopDb();
                    resolve();
                });}
        );
    }

}

module.exports = AppController;