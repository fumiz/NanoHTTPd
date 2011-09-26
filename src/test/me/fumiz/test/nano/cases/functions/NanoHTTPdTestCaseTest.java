package me.fumiz.test.nano.cases.functions;

import me.fumiz.test.nano.lib.NanoHTTPdTestCase;

/**
 * Test TestCase
 * User: fumiz
 * Date: 11/09/23
 * Time: 19:16
 */
public class NanoHTTPdTestCaseTest extends NanoHTTPdTestCase {
    private static final int TEST_PORT_NUMBER = 9801;

    public NanoHTTPdTestCaseTest() {
        super(TEST_PORT_NUMBER);
    }

    public void testPortNumber() {
        assertEquals(TEST_PORT_NUMBER, getPortNumber());
    }
}
