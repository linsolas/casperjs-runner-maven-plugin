casper.test.begin('Fake test from generate-logs/.../test.js', 2, function(test) {
  casper.start('http://www.google.com');

  casper.then(function() {
    test.assert(true, 'true is so true');
    test.assertNot(false, 'false is so wrong');
	this.capture('test.png');
  });

  casper.run(function() {
    test.done();
  });
});