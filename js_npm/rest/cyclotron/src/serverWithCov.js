const app = require("./app");


/* Start server */
var port = app.get('port');
const server = app.listen(port, function(){
    console.log('Cyclotron running on port %d', port);
});


const TB = process.env.TB || 5 //min
setTimeout(function () {
    console.log("stopped by timeout")
    server.close(() => {
        console.log(`app closed after ${TB} min`);
        process.exit(0);
    });},  TB * 60  * 1000)