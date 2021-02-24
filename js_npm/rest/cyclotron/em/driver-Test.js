const AppController = require("./app-driver.js");
const superagent = require("superagent");

const controller = new AppController();
let baseUrlOfSut;


test("test_start_stop", async () => {
    //start
    await controller.setupForGeneratedTest();
    baseUrlOfSut = await controller.startSut();
    expect(baseUrlOfSut).toBeTruthy();

    //isRunning
    expect(controller.isSutRunning()).toBe(true);

    let respone;
    respone = await superagent
        .get(baseUrlOfSut + "/dashboards").set('Accept', "*/*");
    expect(respone.status).toBe(200);

    await controller.stopSut();
    expect(controller.isSutRunning()).toBe(false);

});

