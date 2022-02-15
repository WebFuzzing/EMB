const dbHandler = require("./db-handler");
const {getFreePort} =require("./get-free-port")
const http  = require("http");

const em= require("evomaster-client-js");
const mongoose = require('mongoose');


class AppController extends em.SutController {

    setupForGeneratedTest(){
        return new Promise((resolve)=>{
            this.testcontainer = dbHandler.startDb();
            resolve(this.testcontainer);
        });
    }

    getInfoForAuthentication(){
        let header = new em.dto.HeaderDto();
        header.name = "spaceX-key";
        header.value = "foo";
        let auth = new em.dto.AuthenticationDto();
        auth.name = "spaceX-foo";
        auth.headers = [header];
        return [auth];
    }

    getPreferredOutputFormat() {
        return em.dto.OutputFormat.JS_JEST;
    }

    getProblemInfo() {
        const dto = new em.dto.RestProblemDto();
        dto.openApiUrl = "http://localhost:" + this.port + "/openapi.json";
        return dto;
    }

    isSutRunning(){
        if (!this.server) {
            return false;
        }
        return this.server.listening;
    }

    resetStateOfSUT(){
        return new Promise((async resolve => {
            await dbHandler.cleanDb();
            dbHandler.initAuth('foo');
            resolve();
        }))
    }

    startSut(){
        return new Promise( (resolve) => {
            getFreePort().then((value)=>{
                this.port = value;
                this.server = require("./appAPIs");
                this.server.listen(this.port, "localhost", () => {
                    resolve("http://localhost:" + this.port);
                });
            })
        });
    }

    stopSut() {
        return new Promise( (async resolve => {
                await dbHandler.stopDb();
                this.server.close( () => {
                    process.exit();
                    resolve();
                });
            })
        );
    }
}

module.exports = AppController;