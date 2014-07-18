casper.test.begin('Fake test 1', 1, function(test) {
  casper.echo('I am test1.js in parent dir !');
  test.assert(true);
  test.done();
});