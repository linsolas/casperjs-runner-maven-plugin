file = new File(basedir, 'build.log');

assert file.exists();

assert file.text.contains('casperjs-runner-maven-plugin');
assert file.text.contains('Execution of test anotherTestThatWillRun.js');
assert file.text.contains('Execution of test aTest.coffee');
assert !file.text.contains('Execution of test neither.js');
assert !file.text.contains('Execution of test nope.coffee');
assert file.text.contains('Execution of test test.coffee');
assert file.text.contains('Execution of test Test.js');

assert file.text.contains('Hello from anotherTestThatWillRun.js');
assert file.text.contains('Hello from aTest.coffee');
assert !file.text.contains('Hello from neither.js');
assert !file.text.contains('Hello from nope.coffee');
assert file.text.contains('Hello from test.coffee');
assert file.text.contains('Hello from Test.js');

return true;