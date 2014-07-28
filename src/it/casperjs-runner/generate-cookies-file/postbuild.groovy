test_js_cookies = new File(basedir, 'target/casperjs/testsDir/test_js/cookies_file.txt');
test_coffee_cookies = new File(basedir, 'target/casperjs/testsDir/test_coffee/cookies_file.txt');

assert test_js_cookies.exists();
assert test_coffee_cookies.exists();

return true;