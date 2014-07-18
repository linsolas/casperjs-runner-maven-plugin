casper.test.begin('Fake test', 1, function(test) {
  casper.echo('Hello from otherTestThatWillRun.js');
  test.assert(true);
  test.done();
});