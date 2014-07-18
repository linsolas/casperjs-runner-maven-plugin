casper.test.begin('Fake test', 1, function(test) {
  casper.echo('arg1 of test.js is: ' + casper.cli.get('arg1'));
  casper.echo('arg2 of test.js is: ' + casper.cli.get('arg2'));
  test.assert(true);
  test.done();
});