file = new File(basedir, 'build.log');
assert file.exists();
assert file.text.contains('casperjs-runner-maven-plugin');
assert file.text.contains('Skipping CasperJsRunner execution');

return true;