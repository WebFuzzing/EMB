const em = require("evomaster-client-js");
const dbHandler = require("./db-handler");

const {NestFactory} = require('@nestjs/core');
const superagent = require("superagent");

class AppController extends em.SutController {

    setupForGeneratedTest() {
    }

    getInfoForAuthentication() {
        let jwtLogin = new em.dto.JsonTokenPostLoginDto();
        jwtLogin.endpoint = "/graphql";
        jwtLogin.userId = "foo";
        jwtLogin.extractTokenField = "/data/login/token";
        jwtLogin.jsonPayload = `{
           "query": "mutation{login(data:{email:\\"foo@foo.com\\",password:\\"bar123\\"}){token}}"
        }`;
        jwtLogin.headerPrefix = "Bearer ";


        let auth = new em.dto.AuthenticationDto();
        auth.name = "foo-auth";
        auth.jsonTokenPostLogin = jwtLogin;
        return [auth];
    }

    getPreferredOutputFormat() {
        return em.dto.OutputFormat.JS_JEST;
    }

    getProblemInfo() {
        const dto = new em.dto.GraphQLProblemDto();
        dto.endpoint = "/graphql"
        return dto;
    }

    isSutRunning() {
        if (!this.server) {
            return false;
        }
        return this.server.listening;
    }

    resetStateOfSUT() {
        return new Promise((resolve) => {
            dbHandler.cleanDb().then(async () => {
                await superagent
                    .post(this.baseUrlOfSut + "/graphql")
                    .set('Content-Type', 'application/json')
                    .send(`{
                          "query" : "mutation{createUser(data:{ name:\\"foo\\",username:\\"foo\\", email:\\"foo@foo.com\\",password:\\"bar123\\",confirmPassword:\\"bar123\\"}){username}}"
                         }`)
                    //.ok(res => res.status);
                resolve();
            });
        });
    }

    startSut() {

        return new Promise(async (resolve) => {

            await dbHandler.startDb();

            process.env.SECRET = "a secret";
            process.env.DATABASE_USER = "foo"
            process.env.DATABASE_PASSWORD = "bar"
            process.env.DATABASE_HOST = "localhost"
            process.env.DATABASE_NAME = "db";
            process.env.DATABASE_PORT = process.env.DB_PORT

            const {AppModule} = require('./../src/app.module');

            const app = await NestFactory.create(AppModule);
            app.setGlobalPrefix('api')

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
        return new Promise(async (resolve) => {
            await dbHandler.stopDb();
            this.server.close(() => resolve())
        });
    }

}


module.exports = AppController;