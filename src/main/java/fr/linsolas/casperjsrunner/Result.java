package fr.linsolas.casperjsrunner;

import java.util.concurrent.TimeUnit;

public class Result {

    private int failures = 0;
    private int success = 0;
    private long started = System.nanoTime();

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
                + TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - started) + "ms.";
    }

    public int getFailures() {
        return failures;
    }

    public int getSuccess() {
        return success;
    }

}
