package org.nipu.jmt;

import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;

/**
 * Additional runner for JMH benchmarks.
 *
 * @author Nikita_Puzankov
 */
public class BenchmarkRunner {
    public static void main(String[] args) throws IOException, RunnerException {
        org.openjdk.jmh.Main.main(args);
    }
}
