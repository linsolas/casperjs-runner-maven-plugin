casper.test.begin('Fake test 2', 1, function(test) {
  casper.echo('I am test2.js in sub dir !');
  test.assert(true);
  test.done();
});