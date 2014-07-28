casper.test.begin "Fake test", 2, (test) ->
  casper.start()
  casper.then ->
    test.assert true, "true is so true"
    test.assertNot false, "false is so wrong"
    @capture "test.png"

  casper.run ->
    test.done()