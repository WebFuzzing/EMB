var en = require('../languages/en_US.json');
var vn = require('../languages/vn_VN.json');
console.log(
    Object.keys(en).filter(key => en[key] == vn[key]).map(key => vn[key]).join('\n')
);