file = new File(basedir, 'build.log');
test_js_log = new File(basedir, 'target/casperjs/testsDir/test_js/build.log');
test_coffee_log = new File(basedir, 'target/casperjs/testsDir/test_coffee/build.log');

assert file.exists();
assert file.text.contains('casperjs-runner-maven-plugin');
assert file.text.contains('Execution of test test.js');
assert file.text.contains('Execution of test test.coffee');
assert file.text.contains('PASS 2 tests executed');
assert file.text.contains('2 passed, 0 failed');
assert file.text.contains('Tests run: 2, Success: 2 Failures: 0. Time elapsed:');

assert test_js_log.exists();
assert test_js_log.text.contains('[casperjs --verbose');
assert test_js_log.text.contains('test');
assert test_js_log.text.contains('# Fake test from generate-logs/.../test.js');
assert test_js_log.text.contains('PASS 2 tests executed');
assert test_js_log.text.contains('2 passed, 0 failed');

assert test_coffee_log.exists();
assert test_coffee_log.text.contains('[casperjs --verbose');
assert test_coffee_log.text.contains('test');
assert test_coffee_log.text.contains('# Fake test from generate-logs/.../test.coffee');
assert test_coffee_log.text.contains('PASS 2 tests executed');
assert test_coffee_log.text.contains('2 passed, 0 failed');

return true;