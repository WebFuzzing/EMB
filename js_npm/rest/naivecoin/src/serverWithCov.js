const {app} = require("../src/lib/app");


const port = process.env.PORT || process.env.HTTP_PORT || 3001;
const server = app("localhost", port);
server.listen("localhost", port).then(
    ()=> console.log("http://localhost:" + port)
);

const TB = process.env.TB || 5 //min
setTimeout(function () {
    console.log("stopped by timeout")
    server.stop(() => {
        console.log(`app closed after ${TB} min`);
        process.exit(0);
    });},  TB * 60  * 1000)
