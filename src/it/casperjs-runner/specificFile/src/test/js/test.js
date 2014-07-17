casper.test.begin('Fake test', 1, function(test) {
  casper.echo('Hello test.js');
  test.assert(true);
  test.done();
});