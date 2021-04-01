const net = require('net');

//from https://github.com/sindresorhus/get-port#readme
module.exports ={
    getFreePort: () =>{
        return new Promise( (resolve) => {
            const srv = net.createServer();
            srv.listen(0, ()=>{
                const port = srv.address().port;
                srv.close(()=>{
                    resolve(port)
                })
            });
        });
    }
}
