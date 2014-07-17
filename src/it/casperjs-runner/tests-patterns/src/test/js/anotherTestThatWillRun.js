casper.test.begin('Fake test', 1, function(test) {
  casper.echo('Hello from anotherTestThatWillRun.js');
  test.assert(true);
  test.done();
});