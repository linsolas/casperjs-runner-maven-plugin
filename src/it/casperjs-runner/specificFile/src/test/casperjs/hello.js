casper.test.begin('Fake test', 1, function(test) {
  casper.echo('Hello world !');
  test.assert(true);
  test.done();
});