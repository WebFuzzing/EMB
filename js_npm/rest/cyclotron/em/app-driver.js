const dbHandler = require("./db-handler");
const mongoose = require('mongoose');

const em = require("evomaster-client-js");


class AppController  extends em.SutController {


    getInfoForAuthentication(){
        return [];
    }

    getPreferredOutputFormat() {
        return em.dto.OutputFormat.JS_JEST;
    }

    getProblemInfo() {
        const dto = new em.dto.RestProblemDto();
        dto.openApiUrl = "http://localhost:" + this.port + "/swagger.json";
        return dto;
    }

    isSutRunning(){
        if (!this.server) {
            return false;
        }
        return this.server.listening;
    }

    resetStateOfSUT(){
        return new Promise(async (resolve) => {
            await dbHandler.cleanDb();
            resolve();
        });
    }

    startSut(){
        //docker run -p 27017:27017 mongo
        return new Promise( async (resolve) => {

           await dbHandler.startDb();

            const app =  require("../src/app");

            this.server = app.listen(0, "localhost", () => {
                this.port = this.server.address().port;
                resolve("http://localhost:" + this.port);
            });
        });
    }

    stopSut() {
        return new Promise( (resolve) => {
                this.server.close( () => {
                    // https://mongoosejs.com/docs/api/connection.html#connection_Connection-readyState
                    mongoose.connection.close();
                    dbHandler.stopDb();
                    resolve()
                });

            }
        );
    }

}


module.exports = AppController;