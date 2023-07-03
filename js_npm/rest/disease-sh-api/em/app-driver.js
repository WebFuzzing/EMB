const dbHandler = require("./db-handler");
const em = require("evomaster-client-js");


class AppController extends em.SutController {


    getInfoForAuthentication(){
        return [];
    }

    getPreferredOutputFormat() {
        return em.dto.OutputFormat.JS_JEST;
    }

    getProblemInfo() {
        const dto = new em.dto.RestProblemDto();
        dto.openApiUrl = "http://localhost:" + this.port + "/apidocs/swagger_v3.json";

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
        return new Promise( async (resolve) => {

            await dbHandler.startDb();

            const app = require("../src/server");

            this.port = require('../src/config/index').port;
            this.server = app.listen(0, "localhost", () => {
                this.port = this.server.address().port;
                resolve("http://localhost:" + this.port);
            });
        });
    }

    stopSut() {
        return new Promise( (resolve) => {
                if (this.server) {
                    this.server.close(() => {
                        dbHandler.stopDb();
                        resolve();
                    });
                } else {
                    resolve()
                }
            }
        );
    }

}

module.exports = AppController;