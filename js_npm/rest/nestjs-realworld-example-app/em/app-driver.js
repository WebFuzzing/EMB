const http  = require("http");
const {AddressInfo}  = require("net");

const app = require("../src/dist/server");

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
        return true; //TODO check whether the server is running;
    }

    resetStateOfSUT(){
        return Promise.resolve();
    }

    startSut(){

        //docker run --name mysql_db -e MYSQL_ROOT_PASSWORD=test -e MYSQL_USER=test -e MYSQL_PASSWORD=test  -e MYSQL_DATABASE=test -p 3306:3306 -d mysql:5.7.22
        //note that for this sut, do not support mysql:8.*
        //TODO fix random free port
        this.port = 3000;
        this.server = app.bootstrap(this.port);
        return "http://localhost:" + this.port;

        // return new Promise( (resolve) => {
        //     this.server = app.listen(0, "localhost", () => {
        //         this.port = this.server.address().port;
        //         resolve("http://localhost:" + this.port);
        //     });
        // });
    }

    stopSut() {
        return new Promise( (resolve) => this.server.close( () => resolve()));
    }

}


module.exports = AppController;