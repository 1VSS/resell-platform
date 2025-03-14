package br.com.vss.resell_platform;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

/**
 * Test suite that ensures all tests are discovered and run
 * This is useful for CI/CD pipelines to verify that no tests are skipped
 */
@Suite
@SelectPackages({
    "br.com.vss.resell_platform.controller",
    "br.com.vss.resell_platform.service",
    "br.com.vss.resell_platform.integration"
})
public class AllTestsSuiteIT {
    // This class doesn't need any implementation
    // It serves as a marker for JUnit to run all tests in the specified packages
} 