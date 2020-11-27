const superagent = require("superagent");
const EM = require("evomaster-client-js");
const AppController = require("./app-driver.js");

const controller = new AppController();
let baseUrlOfSut;

/*
    Further check
    https://expressjs.com/en/advanced/healthcheck-graceful-shutdown.html
    https://github.com/godaddy/terminus/blob/master/example/mongoose/express.js
*/

beforeAll( async () => {
    await controller.setupForGeneratedTest();
    baseUrlOfSut = await controller.startSut();
    expect(baseUrlOfSut).toBeTruthy();
});


afterAll( async () => {
    await controller.stopSut();
});


beforeEach(async () =>  {
    await controller.resetStateOfSUT();
});

test("test_invalid_input", async () => {
    let respone;
    try{
        respone = await superagent
            .post(baseUrlOfSut + "/data/G/append").set('Accept', "*/*");
    }catch (e) {
        expect(e.status).toBe(500);
    }
});
