const superagent = require("superagent");
const EM = require("evomaster-client-js");
const AppController = require("./app-driver.js");

const controller = new AppController();
let baseUrlOfSut;
const mongoose = require('mongoose');
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

test("test_get", async () => {
    let respone;
    try{
        respone = await superagent
            .get(baseUrlOfSut + "/data").set('Accept', "*/*");
        expect(e.status).toBe(200);
    }catch (e) {

    }
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


test("test_add_dashboard", async () => {
    let respone;
    try{
        await mongoose.models["dashboard2"].count({  }, function (err, count) {
            expect(count).toBe(0);
        });
        respone = await superagent
            .post(baseUrlOfSut + "/dashboards").set('Accept', "*/*")
            .set('Content-Type','application/json')
            .send(" { " +
                " \"name\": \"foo\", " +
                " \"dashboard\": {" +
                " \"name\": \"bar\" " +
                " } " +
                " } ");
        expect(respone.status).toBe(200);
        await mongoose.models["dashboard2"].count({ name: 'bar' }, function (err, count) {
            expect(count).toBe(1);
        });

    }catch (e) {
        console.error(e)
    }
});

test("test_add_2dashboard", async () => {
    let respone;
    try{
        await mongoose.models["dashboard2"].count({  }, function (err, count) {
            expect(count).toBe(0);
        });
        respone = await superagent
            .post(baseUrlOfSut + "/dashboards").set('Accept', "*/*")
            .set('Content-Type','application/json')
            .send(" { " +
                " \"name\": \"foo\", " +
                " \"dashboard\": {" +
                " \"name\": \"bar\" " +
                " } " +
                " } ");
        expect(respone.status).toBe(200);

        await superagent
            .post(baseUrlOfSut + "/dashboards").set('Accept', "*/*")
            .set('Content-Type','application/json')
            .send(" { " +
                " \"name\": \"foo2\", " +
                " \"dashboard\": {" +
                " \"name\": \"bar2\" " +
                " } " +
                " } ");
        await mongoose.models["dashboard2"].count({}, function (err, count) {
            expect(count).toBe(2);
        });

    }catch (e) {
        console.error(e)
    }
});