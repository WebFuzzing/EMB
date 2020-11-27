const http  = require("http");
const {AddressInfo}  = require("net");
const app = require("../src/server");

const em = require("evomaster-client-js");
const config = require('../src/config/index')

const baseURL = "https://disease.sh/"


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
        //"http://localhost:" + this.port
        dto.swaggerJsonUrl = baseURL + "/apidocs/swagger_v3.json";

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

        new Promise( (resolve) => {
            this.server = app.listen(0, "localhost", () => {
                this.port = config.port;
                resolve("http://localhost:" + this.port);
            });
        });

        return baseURL
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