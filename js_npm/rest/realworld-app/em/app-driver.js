require('ts-node/register');

const dbHandler = require("./db-handler");
const em = require("evomaster-client-js");
const superagent = require("superagent");
const {getConnectionOptions} = require("typeorm");
const {NestFactory} = require("@nestjs/core");


class AppController  extends em.SutController {

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
        dto.openApiUrl = "http://localhost:" + this.port + "/swagger.json";

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

        return new Promise(async (resolve) => {

            //docker run --name mysql_db -e MYSQL_ROOT_PASSWORD=test -e MYSQL_USER=test -e MYSQL_PASSWORD=test  -e MYSQL_DATABASE=test -p 3306:3306 -d mysql:5.7.22
            //note that for this sut, do not support mysql:8.*
            await dbHandler.startDb();

            const connectionOptions = await getConnectionOptions();
            // modify the value of port
            Object.assign(connectionOptions, { port: process.env.DB_PORT || 3306});

            const {ApplicationModule} = require("../src/app.module");

            const appOptions = {cors: true};
            const app = await NestFactory.create(ApplicationModule, appOptions);
            app.setGlobalPrefix('api');

            app.use('/swagger.json', (req, res) => {
                res.status(200);
                res.json(require('../swagger.json'));
            });

            app.listen(0, "localhost", () => {
                this.server = app.getHttpServer();
                this.port = this.server.address().port;
                const url = "http://localhost:" + this.port;
                this.baseUrlOfSut = url;
                console.log("Started API at: " + url)
                resolve(url);
            });
        });

    }

    stopSut() {
        return new Promise( (async resolve => {
                await dbHandler.stopDb();
                this.server.close(() => resolve())
            })
        );
    }

}


module.exports = AppController;