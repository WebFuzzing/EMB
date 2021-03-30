import HeaderDto from "../../../evomaster-client-js/dist/controller/api/dto/HeaderDto";
import AuthenticationDto from "../../../evomaster-client-js/dist/controller/api/dto/AuthenticationDto";

const dbHandler = require("./db-handler");
const http  = require("http");

const em = require("evomaster-client-js");
const mongoose = require('mongoose');

class AppController extends em.SutController {

    setupForGeneratedTest(){
        return new Promise((resolve)=>{
            this.testcontainer = dbHandler.startDb();
            resolve(this.testcontainer);
        });
    }

    getInfoForAuthentication(){
        return [new AuthenticationDto('spaceX', [new HeaderDto('spacex-key', 'foo')], null)];
    }

    getPreferredOutputFormat() {
        return em.dto.OutputFormat.JS_JEST;
    }

    getProblemInfo() {
        const dto = new em.dto.RestProblemDto();
        dto.swaggerJsonUrl = "http://localhost:" + this.port + "/openapi.json";

        return dto;
    }

    isSutRunning(){
        if (!this.server) {
            return false;
        }
        return this.server.listening;
    }

    resetStateOfSUT(){
        dbHandler.cleanDb();
        dbHandler.initAuth('foo');
        return Promise.resolve();
    }

    startSut(){
        //TODO get free tcp port
        return new Promise( (resolve) => {
            this.server = require("./appAPIs");
            this.port = 6673;
            this.server.listen(this.port, "localhost", () => {
                resolve("http://localhost:" + this.port);
            });
        });
    }

    stopSut() {
        return new Promise( (resolve) => {
                mongoose.connection.close(false, () => {
                    dbHandler.stopDb();
                    this.server.close(() => {
                        logger.info('Shutting down...');
                        process.exit();
                    });
                });
            }
        );
    }

}

module.exports = AppController;