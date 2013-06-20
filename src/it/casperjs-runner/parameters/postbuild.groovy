file = new File(basedir, 'build.log');
assert file.exists();
assert file.text.contains('casperjs-runner-maven-plugin');
assert file.text.contains('Execution of test test.js');
assert file.text.contains('Execution of test test.coffee');
assert file.text.contains('arg1 of test.js is: Hello');
assert file.text.contains('arg2 of test.js is: World');
assert file.text.contains('arg1 of test.coffee is: Hello');
assert file.text.contains('arg2 of test.coffee is: World');

return true;