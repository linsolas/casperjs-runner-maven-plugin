casper.test.begin('Generating capture', 2, function(test) {
  casper.start();

  casper.then(function() {
    test.assert(true, 'true is so true');
	test.assertNot(false, 'false is so wrong');
	this.capture('test.png');
  });

  casper.run(function() {
    test.done();
  });
});