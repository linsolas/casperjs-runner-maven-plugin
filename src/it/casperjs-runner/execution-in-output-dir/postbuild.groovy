file = new File(basedir, 'build.log');
assert file.exists();
assert file.text.contains('casperjs-runner-maven-plugin');
assert file.text.contains('Tests run: 2, Success: 2 Failures: 0. Time elapsed:');

capture_js = new File(basedir, 'target/casperjs/testsDir/test_capture_js/test.png');
capture_coffee = new File(basedir, 'target/casperjs/testsDir/test_capture_coffee/test.png');

assert capture_js.exists();
assert capture_coffee.exists();

return true;