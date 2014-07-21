var conf = {};

casper.test.begin('Fake test', 1, function(test) {
  casper.echo("Raw conf is: " + casper.cli.get("conf"));
  try {
    conf = JSON.parse(casper.cli.get("conf"));
  } catch(err) {
    casper.echo("Unable to decode Json from 'conf' parameter : "+err+". Default configuration will be loaded", "error");
  }

  casper.echo('conf.key from test.js is: ' + conf.key);

  test.assert(true);
  test.done();
});