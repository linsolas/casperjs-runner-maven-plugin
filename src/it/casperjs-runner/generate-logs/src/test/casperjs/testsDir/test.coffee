casper.test.begin 'Fake test from generate-logs/.../test.coffee', 2, (test) ->
  test.assert true, 'true is so true'
  test.assertNot false, 'false is so wrong'
  test.done()
