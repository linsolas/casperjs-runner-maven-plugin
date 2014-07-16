var casper = require('casper').create();
var system = require('system');

casper.echo('envKey from test.js is: ' + system.env.envKey);

casper.exit();