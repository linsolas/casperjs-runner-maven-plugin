casper.test.begin 'Fake test', 1, (test) ->
  casper.echo 'Hello test.coffee'
  test.assert true
  test.done()
