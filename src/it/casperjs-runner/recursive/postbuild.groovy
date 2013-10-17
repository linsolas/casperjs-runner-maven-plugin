file = new File(basedir, 'build.log');
assert file.exists();
assert file.text.contains('casperjs-runner-maven-plugin');
assert file.text.contains('Execution of test test1.js');
assert file.text.contains('Execution of test test1.coffee');
assert file.text.contains('Execution of test test2.js');
assert file.text.contains('Execution of test test2.coffee');
assert file.text.contains('I am test1.js in parent dir !');
assert file.text.contains('I am test1.coffee in parent dir !');
assert file.text.contains('I am test2.js in sub dir !');
assert file.text.contains('I am test2.coffee in sub dir !');

return true;