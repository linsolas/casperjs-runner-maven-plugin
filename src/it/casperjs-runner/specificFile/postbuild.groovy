file = new File(basedir, 'build.log');

assert file.exists();

assert file.text.contains('casperjs-runner-maven-plugin');
assert !file.text.contains('Execution of test test.js');
assert !file.text.contains('Execution of test test.coffee');
assert file.text.contains('Execution of test hello.js');

assert !file.text.contains('Hello test.js');
assert !file.text.contains('Hello test.coffee');
assert file.text.contains('Hello world !');

return true;