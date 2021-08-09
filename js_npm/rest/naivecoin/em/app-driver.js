const http  = require("http");
const {AddressInfo}  = require("net");

const {app, reset} = require("../src/lib/app");

const em = require("evomaster-client-js");
const {getFreePort} =require("./get-free-port")
const fs = require('fs-extra');

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
        return new Promise(async (resolve) => {
            let file = 'data';
            // if (fs.existsSync(file))
            fs.removeSync(file);
            reset(this.server);
            resolve(file);

        });
        // return Promise.resolve();
    }

    startSut(){
        return new Promise((resolve) =>  {
            getFreePort().then((value) => {
                this.port = value;
                this.server=app("localhost", this.port);
                this.server.listen("localhost", this.port).then(
                    ()=> resolve("http://localhost:" + this.port)
                );
            })
        });
    }

    stopSut() {
        this.server.stop();
    }

}


module.exports = AppController;