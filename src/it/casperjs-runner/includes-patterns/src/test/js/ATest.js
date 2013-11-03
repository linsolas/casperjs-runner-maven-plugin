casper.start();

casper.then(aStepFunction);
casper.then(bStepFunction);

casper.run(function() {
  this.test.done(2);
  this.test.renderResults(true);
});