casper.test.begin('Fake test', 2, function(test) {
  casper.aStepFunction(test);
  casper.bStepFunction(test);
  test.done();
});