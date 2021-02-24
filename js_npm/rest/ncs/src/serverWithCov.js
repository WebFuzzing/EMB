const app  = require("./app");

const port = process.env.PORT || 8080;

const server = app.listen(port, () => {
    console.log("Started RESTful API on port " + port);
});


const TB = process.env.TB || 5 //min
setTimeout(function () {
    console.log("stopped by timeout")
    server.close(() => {
        console.log(`app closed after ${TB} min`);
        process.exit(0);
    });},  TB * 60  * 1000)