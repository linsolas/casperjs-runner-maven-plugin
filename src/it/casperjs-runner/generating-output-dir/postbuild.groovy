file = new File(basedir, 'build.log');
assert file.exists();
assert file.text.contains('casperjs-runner-maven-plugin');
assert file.text.contains('Tests run: 4, Success: 4 Failures: 0. Time elapsed:');

assert new File(basedir, 'target/casperjs/testsDir/test_js').isDirectory();
assert new File(basedir, 'target/casperjs/testsDir/test_coffee').isDirectory();
assert new File(basedir, 'target/casperjs/testsDir/test_test_js').isDirectory();
assert new File(basedir, 'target/casperjs/testsDir/subdir/test_js').isDirectory();

return true;