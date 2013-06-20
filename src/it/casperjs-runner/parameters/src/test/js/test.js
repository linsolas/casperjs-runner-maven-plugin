var casper = require('casper').create();

casper.echo('arg1 of test.js is: ' + casper.cli.get(0));
casper.echo('arg2 of test.js is: ' + casper.cli.get(1));

casper.exit();