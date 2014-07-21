casper.test.begin 'Fake test', 2, (test) ->
  casper.stepFunction test
  test.done()
