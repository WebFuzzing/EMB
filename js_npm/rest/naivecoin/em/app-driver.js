const http  = require("http");
const {AddressInfo}  = require("net");

const app = require("../src/lib/app");

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
        return this.server.isRunning();
    }

    resetStateOfSUT(){
        return Promise.resolve();
    }

    startSut(){
        //TODO employ random tcp port
        this.port = 3001;
        this.server=app("localhost", this.port);
        this.server.listen("localhost", this.port);

        return "http://localhost:" + this.port;
    }

    stopSut() {
        this.server.stop();
        //return new Promise( (resolve) => this.server.close( () => resolve()));
    }

}


module.exports = AppController;