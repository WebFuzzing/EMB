const http  = require("http");
const {AddressInfo}  = require("net");

const app = require("../src/dist/server");

const em = require("evomaster-client-js");
const {getFreePort} =require("./get-free-port")

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
        //TODO clean mysql db
        //docker run --name mysql_db -e MYSQL_ROOT_PASSWORD=test -e MYSQL_USER=test -e MYSQL_PASSWORD=test  -e MYSQL_DATABASE=test -p 3306:3306 -d mysql:5.7.22
        //note that for this sut, do not support mysql:8.*
        return new Promise((resolve) => {
            getFreePort().then((value)=>{
                this.port = value;
                this.server = app.bootstrap(this.port);
                resolve("http://localhost:" + this.port);
            });
        });
    }

    stopSut() {
        return new Promise( (resolve) => this.server.close( () => resolve()));
    }

}


module.exports = AppController;