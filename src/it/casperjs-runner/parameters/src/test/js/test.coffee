casper = require('casper').create()

casper.echo 'arg1 of test.coffee is: ' + casper.cli.get 'arg1'
casper.echo 'arg2 of test.coffee is: ' + casper.cli.get 'arg2'

casper.exit 0