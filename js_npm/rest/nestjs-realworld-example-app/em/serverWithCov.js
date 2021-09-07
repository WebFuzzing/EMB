(async ()=>{
    const AppController = require("./app-driver");
    const app = new AppController();

    await app.setupForGeneratedTest();

    // configure SUT_PORT for this sut
    await app.startSut()
    await app.resetStateOfSUT()

    const TB = process.env.TB || 5 //min
    setTimeout(function () {
        console.log("stopped by timeout");
        app.stopSut();
    },  TB * 60  * 1000)
})();


