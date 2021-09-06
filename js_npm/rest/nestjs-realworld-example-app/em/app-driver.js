const dbHandler = require("./db-handler");
const http  = require("http");
const {AddressInfo}  = require("net");

// const app = require("../src/dist/server");

const em = require("evomaster-client-js");
const superagent = require("superagent");
const {getFreePort} =require("./get-free-port")

class AppController  extends em.SutController {

    setupForGeneratedTest(){
        return new Promise((resolve)=>{
            this.testcontainer = dbHandler.startDb();
            resolve(this.testcontainer);
        });
    }

    getInfoForAuthentication(){
        let jwtLogin = new em.dto.JsonTokenPostLoginDto();
        jwtLogin.endpoint ="/api/users/login";
        jwtLogin.userId="foo";
        jwtLogin.extractTokenField="/user/token";
        jwtLogin.jsonPayload = `{
          "user":{
            "email": "foo@foo.foo",
            "password": "foofoo"
          }
        }`;
        jwtLogin.headerPrefix="token ";

        // let header = new em.dto.HeaderDto();
        // header.name = "Authorization";
        // header.value = "Token jwt.token.here"
        let auth = new em.dto.AuthenticationDto();
        auth.name = "foo-auth";
        // auth.headers = [header];
        auth.jsonTokenPostLogin =jwtLogin;
        return [auth];
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
        return new Promise((resolve)=>{
            dbHandler.cleanDb().then(async ()=>{
                await superagent
                    .post(this.baseUrlOfSut + "/api/users")
                    .set('Content-Type','application/json')
                    .send(" { " +
                        " \"user\": { " +
                        " \"username\": \"foo\", " +
                        " \"email\": \"foo@foo.foo\", " +
                        " \"password\": \"foofoo\" " +
                        " } " +
                        " } ")
                    .ok(res => res.status);
                resolve();
            });
        });
    }

    startSut(){
        //TODO clean mysql db
        //docker run --name mysql_db -e MYSQL_ROOT_PASSWORD=test -e MYSQL_USER=test -e MYSQL_PASSWORD=test  -e MYSQL_DATABASE=test -p 3306:3306 -d mysql:5.7.22
        //note that for this sut, do not support mysql:8.*
        return new Promise(async (resolve) => {
            this.port = process.env.SUT_PORT || await getFreePort();
            this.server = await require("../src/dist/server").bootstrap(this.port);
            this.baseUrlOfSut = "http://localhost:" + this.port;
            resolve("http://localhost:" + this.port);
        });

    }

    stopSut() {
        return new Promise( (async resolve => {
                await dbHandler.stopDb();
                await require("../src/dist/server").stop().then(()=>{
                    // process.exit();
                    resolve();
                });

            })
        );
    }

}


module.exports = AppController;