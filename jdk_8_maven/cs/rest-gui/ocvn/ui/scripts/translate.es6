const readline = require('readline');
const fs = require('fs');
let translations = require('../languages/vn_VN.json');
let input = [];

const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout
});

rl.prompt();

rl.on('line', line => input.push(line));

rl.on('close', () => {
  let translationHash = {};
  input.forEach(translation => {
    let [eng, viet] = translation.split('\t');
    translationHash[eng] = viet;
  });

  Object.keys(translations).forEach(key => translations[key] = translationHash[translations[key]] || translations[key]);

  const json = JSON.stringify(translations, null, '\t');
  fs.writeFile('languages/vn_VN.json', json, 'utf8', (err) => err ? console.error('Oops', err) : console.log('Done!'));
});