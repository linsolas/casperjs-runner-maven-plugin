file = new File(basedir, 'build.log');
assert file.exists();
assert file.text.contains('casperjs-runner-maven-plugin');
assert file.text.contains('Execution of test test.js');
assert !file.text.contains('Execution of test test.coffee');
assert file.text.contains('PASS 2 tests executed');
assert file.text.contains('2 passed, 0 failed');
assert file.text.contains('Tests run: 1, Success: 1 Failures: 0. Time elapsed:');

return true;