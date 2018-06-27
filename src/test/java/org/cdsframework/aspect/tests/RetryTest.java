/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdsframework.aspect.tests;

import junit.framework.TestCase;

/**
 *
 * @author HLN Consulting LLC
 */
public class RetryTest extends TestCase {
    
    public RetryTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

//    /**
//     * Test of doSomething method, of class RetryPojo.
//     */
//    public void testDoSomethingX() {
//        boolean success = false;
//        try {
//            RetryPojo instance = new RetryPojo();
//            instance.doSomethingX();
//        }
//        catch (java.lang.ArithmeticException e) {
//            success = true;
//        }
//        if (!success) {
//            fail("The test case failed as it did not return an java.lang.ArithmeticException.");
//        }
//    }
//    
//    /**
//     * Test of doSomethingY method, of class RetryPojo.
//     */
//    public void testDoSomethingY() {
//        boolean success = false;
//        try {
//            RetryPojo instance = new RetryPojo();
//            instance.doSomethingY();
//        }
//        catch (org.omg.CORBA.COMM_FAILURE e) {
//            success = true;
//        }
//        if (!success) {
//            fail("The test case failed as it did not return an org.omg.CORBA.COMM_FAILURE.");
//        }
//    }
    
    /**
     * Test of doSomethingZ method, of class RetryPojo.
     */
    public void testDoSomethingZ() {
        boolean success = false;
        try {
            RetryPojo instance = new RetryPojo();
            instance.doSomethingZ();
        }
        catch (java.lang.ArithmeticException e) {
            success = true;
        }
        if (!success) {
            fail("The test case failed as it did not return an org.omg.CORBA.COMM_FAILURE.");
        }
    }    
    
    
    /**
     * Test of doSomething method, of class RetryPojo.
     */
    public void testDoSomething() {
        boolean success = false;
        try {
            RetryPojo instance = new RetryPojo();
            instance.doSomething(3);
        }
        catch (java.lang.ArithmeticException e) {
            success = true;
        }
        if (!success) {
            fail("The test case failed as it did not return an java.lang.ArithmeticException.");
        }
    }
}
