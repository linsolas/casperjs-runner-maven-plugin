casper.test.begin 'Fake test 1', 1, (test) ->
  casper.echo 'I am test1.coffee in parent dir !'
  test.assert true
  test.done()
