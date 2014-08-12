casper.test.begin('Subdir fake js test', 2, function(test) {
  test.assert(true, 'true is so true');
  test.assertNot(false, 'false is so wrong');
  test.done();
});