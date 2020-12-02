const http  = require("http");
const {AddressInfo}  = require("net");
const app = require("../src/server");

const em = require("evomaster-client-js");
const config = require('../src/config/index')

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

        return new Promise( (resolve) => {
            this.port = config.port;
            this.server = app.listen(this.port, "localhost", () => {
                resolve("http://localhost:" + this.port);
            });
        });
    }

    stopSut() {
        return new Promise( (resolve) =>
            {
                this.server.close( () => resolve());
            }
        );
    }

}

module.exports = AppController;