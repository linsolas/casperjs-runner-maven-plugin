package fr.linsolas.casperjsrunner;

public class Result {

    private int failures = 0;
    private int success = 0;
    private long started = System.currentTimeMillis();

    public void add(Result other) {
        failures += other.getFailures();
        success += other.getSuccess();
    }

    public void addSuccess() {
        success++;
    }

    public void addFailure() {
        failures++;
    }

    public String print() {
        return "Tests run: " + (failures + success) + ", Success: " + success
                + " Failures: " + failures + ". Time elapsed: "
                + (System.currentTimeMillis() - started) + "ms.";
    }

    public int getFailures() {
        return failures;
    }

    public int getSuccess() {
        return success;
    }

}
