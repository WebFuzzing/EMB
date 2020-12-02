const app = require("./app");


/* Start server */
var port = app.get('port');
app.listen(port, function(){
    console.log('Cyclotron running on port %d', port);
});