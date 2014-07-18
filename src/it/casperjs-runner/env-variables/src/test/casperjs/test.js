var system = require('system');

casper.test.begin('Fake test', 1, function(test) {
  casper.echo('envKey from test.js is: ' + system.env.envKey);
  test.assert(true);
  test.done();
});