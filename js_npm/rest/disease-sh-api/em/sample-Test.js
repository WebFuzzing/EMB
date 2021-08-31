const superagent = require("superagent");
const EM = require("evomaster-client-js");
const AppController = require("./app-driver.js");

const controller = new AppController();
let baseUrlOfSut;

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

test("test_get_operation", async () => {
    let respone;
    respone = await superagent
        .get(baseUrlOfSut + "/v3/covid-19/all").set('Accept', "*/*");
    console.log(respone);
    expect(respone.statusCode).toBe(200);
});


jest.setTimeout(50000);