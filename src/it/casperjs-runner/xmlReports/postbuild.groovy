file = new File(basedir, 'build.log');
assert file.exists();
assert file.text.contains('casperjs-runner-maven-plugin');
assert file.text.contains('Execution of test test.js');
assert file.text.contains('Execution of test test.coffee');
assert file.text.contains('PASS 2 tests executed');
assert file.text.contains('2 passed, 0 failed');
assert file.text.contains('Tests run: 4, Success: 4 Failures: 0. Time elapsed:');

file = new File(basedir, 'target/casperjs-reports/TEST-test_coffee.xml');
assert file.exists();
assert file.text.contains('Fake coffee test');

file = new File(basedir, 'target/casperjs-reports/TEST-test_js.xml');
assert file.exists();
assert file.text.contains('Fake js test');

file = new File(basedir, 'target/casperjs-reports/TEST-subdir_test_coffee.xml');
assert file.exists();
assert file.text.contains('Subdir fake coffee test');

file = new File(basedir, 'target/casperjs-reports/TEST-subdir_test_js.xml');
assert file.exists();
assert file.text.contains('Subdir fake js test');

return true;