var casper = require('casper').create();

casper.start('http://www.google.fr', function() {
  this.test.assert(true, "true is so true");
  this.test.assert(false, "false is wrong");
});

casper.run(function() {
  this.test.done(2);
  this.test.renderResults(true);
});