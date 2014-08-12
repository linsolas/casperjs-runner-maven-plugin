casper.test.begin 'Subdir fake coffee test', 2, (test) ->
  test.assert true, 'true is so true'
  test.assertNot false, 'false is so wrong'
  test.done()
