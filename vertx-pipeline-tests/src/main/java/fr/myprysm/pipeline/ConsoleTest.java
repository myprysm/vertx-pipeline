/*
 * Copyright 2018 the original author or the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.myprysm.pipeline;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Base Console test.
 * <p>
 * Allows to perform assertions on the console output. Please use this class with care
 * as the standard output is redirected to an in-memory buffer thus if the output is very verbose
 * you can encounter some {@link OutOfMemoryError}.
 */
public abstract class ConsoleTest {
    private PrintStream stream;
    private PrintStream console;
    private ByteArrayOutputStream out;
    private static final Logger LOG = LoggerFactory.getLogger(ConsoleTest.class);

    @BeforeEach
    void setUp() {
        LOG.info("Replacing standard output by a custom output stream");
        out = new ByteArrayOutputStream();
        stream = new PrintStream(out, true);
        console = System.out;
        System.setOut(stream);
    }

    /**
     * Returns the console output.
     * If <code>reset</code> is set to <code>true</code>, then the console buffer is reset.
     *
     * @param reset <code>true</code> to reset console after getting output
     * @return the console output
     */
    protected String getConsoleOutput(boolean reset) {
        String output = out.toString();
        if (reset) {
            reset();
        }
        return output;
    }

    /**
     * Asserts that the console output contains a line matching the provided pattern.
     * A {@link Pattern} is used with <code>MULTILINE</code> option to validate against console output.
     *
     * @param pattern the pattern to test
     */
    protected void assertConsoleContainsPattern(String pattern) {
        assertConsoleContainsPattern(Pattern.compile(pattern, Pattern.MULTILINE));
    }

    /**
     * Asserts that the console output contains the provided {@link Pattern}.
     *
     * @param pattern the pattern to test
     */
    protected void assertConsoleContainsPattern(Pattern pattern) {
        assertThat(pattern.matcher(getConsoleOutput()).find()).isTrue();
    }

    /**
     * Asserts that the console output does not contain a line matching the provided pattern.
     * A {@link Pattern} is used with <code>MULTILINE</code> option to validate against console output.
     *
     * @param pattern the pattern to test
     */
    protected void assertConsoleDoesNotContainPattern(String pattern) {
        assertConsoleDoesNotContainPattern(Pattern.compile(pattern, Pattern.MULTILINE));
    }

    /**
     * Asserts that the console output does not contain the provided {@link Pattern}.
     *
     * @param pattern the pattern to test
     */
    protected void assertConsoleDoesNotContainPattern(Pattern pattern) {
        assertThat(pattern.matcher(getConsoleOutput()).find()).isFalse();
    }

    /**
     * Returns the console output.
     *
     * @return the console output
     */
    protected String getConsoleOutput() {
        return getConsoleOutput(false);
    }

    /**
     * Returns the console output and reset the buffer.
     *
     * @return the console output
     */
    protected String getConsoleOutputAndReset() {
        return getConsoleOutput(true);
    }

    /**
     * Reset the console buffer
     */
    protected void reset() {
        if (out != null) {
            out.reset();
        }
    }

    @AfterEach
    void tearDown() {
        stream.close();
        System.setOut(console);
        LOG.info("Console reset");
    }
}
