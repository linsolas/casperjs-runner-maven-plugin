file = new File(basedir, 'build.log');
assert file.exists();
assert file.text.contains('casperjs-runner-maven-plugin');
assert file.text.contains('--verbose');
assert file.text.contains('--log-level=debug');
return true;