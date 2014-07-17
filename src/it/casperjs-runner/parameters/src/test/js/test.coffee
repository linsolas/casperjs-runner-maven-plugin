casper.test.begin 'Fake test', 1, (test) ->
  casper.echo 'arg1 of test.coffee is: ' + casper.cli.get 'arg1'
  casper.echo 'arg2 of test.coffee is: ' + casper.cli.get 'arg2'
  test.assert true
  test.done()
