const em = require("evomaster-client-js);
const dbHandler = require("./db-handler");

const { NestFactory } = require('@nestjs/core');
const { AppModule } =  require('./../src/app.module');

class AppController  extends em.SutController {

    setupForGeneratedTest(){
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
        return dbHandler.cleanDb();
    }

    startSut(){

        return new Promise( async (resolve) =>  {

            await dbHandler.startDb();

            process.env.SECRET = "a secret";
            process.env.DATABASE_USER = "foo"
            process.env.DATABASE_PASSWORD = "bar"
            process.env.DATABASE_HOST="localhost"
            process.env.DATABASE_NAME="db";
            process.env.DATABASE_PORT=process.env.DB_PORT

            const app = await NestFactory.create(AppModule);
            app.setGlobalPrefix('api')

            app.listen(0, "localhost", () => {
                this.server = app.server;
                this.port = this.server.address().port;
                resolve("http://localhost:" + this.port);
            });
        });
    }

    stopSut() {
        return new Promise( async (resolve) => {
            await dbHandler.stopDb();
            this.server.close( () => resolve())
        });
    }

}


module.exports = AppController;