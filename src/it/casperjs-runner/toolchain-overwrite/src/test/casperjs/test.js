casper.test.begin('Fake test', 2, function(test) {
  test.assert(true, 'true is so true');
  test.assertNot(false, 'false is so wrong');
  test.done();
});