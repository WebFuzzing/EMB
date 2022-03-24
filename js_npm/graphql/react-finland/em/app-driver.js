const http  = require("http");
const {AddressInfo}  = require("net");

const em = require("evomaster-client-js");

const createApp = require("../server/app").default;



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
        const dto = new em.dto.GraphQLProblemDto();
        dto.endpoint = "/graphql"
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

        return new Promise( async (resolve) =>  {

            const app = await createApp();

            this.server = app.listen(0, "localhost", () => {
                this.port = this.server.address().port;
                resolve("http://localhost:" + this.port);
            });
        });
    }

    stopSut() {
        return new Promise( (resolve) => this.server.close( () => resolve()));
    }

}


module.exports = AppController;