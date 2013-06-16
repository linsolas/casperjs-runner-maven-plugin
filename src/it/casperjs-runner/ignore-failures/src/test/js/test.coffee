casper = require('casper').create()

casper.start 'http://www.google.fr', ->
  this.test.assert true, "true is so true"
  this.test.assert false, "false is wrong"

casper.run ->
  this.test.done 2
  this.test.renderResults true
