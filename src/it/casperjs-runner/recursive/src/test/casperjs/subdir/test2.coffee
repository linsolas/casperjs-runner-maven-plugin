casper.test.begin 'Fake test 2', 1, (test) ->
  casper.echo 'I am test2.coffee in sub dir !'
  test.assert true
  test.done()
