file = new File(basedir, 'build.log');
assert file.exists();
assert file.text.contains('casperjs-runner-maven-plugin');
assert file.text.contains('Execution of test test.js');
assert file.text.contains('Test \'test.js\' has failure');
assert file.text.contains('Execution of test test.coffee');
assert file.text.contains('Test \'test.coffee\' has failure');
assert file.text.contains('FAIL 2 tests executed');
assert file.text.contains('1 passed, 1 failed');
assert file.text.contains('Tests run: 2, Success: 0 Failures: 2. Time elapsed:');

return true;