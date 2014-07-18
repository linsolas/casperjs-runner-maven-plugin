casper.test.begin 'Fake test', 1, (test) ->
  casper.echo 'Hello from nope.coffee'
  test.assert true
  test.done()
