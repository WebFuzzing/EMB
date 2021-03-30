const http  = require("http");

const em = require("evomaster-client-js");
const mongoose = require('mongoose');
const appAPIs = require("./appAPIs")

class AppController  extends em.SutController {

    setupForGeneratedTest(){
        return Promise.resolve();
    }

    getInfoForAuthentication(){
        //TODO add auth info
        return [];
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
        //TODO db cleaner and insert auth data
        return Promise.resolve();
    }

    startSut(){
        //TODO get free tcp port
        return new Promise( (resolve) => {
            this.server = appAPIs;
            this.port = 6673;
            this.server.listen(this.port, "localhost", () => {
                resolve("http://localhost:" + this.port);
            });
        });
    }

    stopSut() {
        return new Promise( (resolve) => {
                mongoose.connection.close(false, () => {
                    logger.info('Mongo closed');
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