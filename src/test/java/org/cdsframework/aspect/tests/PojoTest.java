/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdsframework.aspect.tests;

import junit.framework.TestCase;

/**
 *
 * @author HLN Consulting, LLC
 */
public class PojoTest extends TestCase {
    
    public PojoTest(String testName) {
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
    
    public void testCreatePojoAndCallSetter() {
        boolean success = false;
        PropertySetterPojo pojo = new PropertySetterPojo();
        pojo.setProperty3("test prop3");
        pojo.setProperty2("pete");
        pojo.setProperty1("test sam");
        long start = System.nanoTime();
        for (int i = 0; i < 10000;) {
            pojo.setProperty1("test sam" + i);
            i ++;
        }
        System.out.println(false + " Leaving setProperty1 " + (System.nanoTime() - start)/1000000.0 + " ms");

        pojo.setProperty3("test sam");
        start = System.nanoTime();
        for (int i = 0; i < 10000;) {
            pojo.setProperty3("test sam" + i);
            i ++;
        }
        System.out.println(false + " Leaving setProperty3 " + (System.nanoTime() - start)/1000000.0 + " ms");

        start = System.nanoTime();
        for (int i = 0; i < 1;) {
            pojo.setProperty1("test sam" + i);
            i ++;
        }
        System.out.println(false + " Leaving setProperty1 " + (System.nanoTime() - start)/1000000.0 + " ms");

        pojo.setProperty3("test sam");
        start = System.nanoTime();
        for (int i = 0; i < 1;) {
            pojo.setProperty3("test sam" + i);
            i ++;
        }
        System.out.println(false + " Leaving setProperty3 " + (System.nanoTime() - start)/1000000.0 + " ms");
        
        success = true;
        if (!success) {
            fail("The test case failed as it did not return an org.omg.CORBA.COMM_FAILURE.");
        }
    }    
    
    public void testCreatePojoAndCallSetterStringWithLeadingAndTrailingSpaces() {
        PropertySetterPojo pojo = new PropertySetterPojo();
        String testString = "   testing123     ";
        pojo.setProperty1(testString);
        String property1 = pojo.getProperty1();
        System.out.println("property1=" + property1);
        assertTrue(property1.equals(testString.trim()));
    }        

    public void testCreatePojoAndCallSetterStringWithTrailingSpaces() {
        PropertySetterPojo pojo = new PropertySetterPojo();
        String testString = "testing123     ";
        pojo.setProperty1(testString);
        String property1 = pojo.getProperty1();
        System.out.println("property1=" + property1);
        assertTrue(property1.equals(testString.trim()));
    }        
    
    public void testCreatePojoAndCallSetterStringWithLeadingSpaces() {
        PropertySetterPojo pojo = new PropertySetterPojo();
        String testString = "      testing123";
        pojo.setProperty1(testString);
        String property1 = pojo.getProperty1();
        System.out.println("property1=" + property1);
        assertTrue(property1.equals(testString.trim()));
    }        
    
    public void testCreatePojoAndCallSetterEmptyString() {
        PropertySetterPojo pojo = new PropertySetterPojo();
        String testString = "";
        pojo.setProperty1(testString);
        String property1 = pojo.getProperty1();
        System.out.println("property1=" + property1);
        assertNull(property1);
    }        
        
    public void testCreatePojoAndCallSetterNullString() {
        PropertySetterPojo pojo = new PropertySetterPojo();
        String testString = null;
        pojo.setProperty1(testString);
        String property1 = pojo.getProperty1();
        System.out.println("property1=" + property1);
        assertNull(property1);
    }         
    
    public void testCreatePojoAndCallSetterDontTrimString() {
        PropertySetterPojo pojo = new PropertySetterPojo();
        String testString = " ad adad asf 45      ";
        pojo.setProperty4(testString);
        String property4 = pojo.getProperty4();
        System.out.println("property4=" + property4);
        assertTrue(property4.equals(testString));
    }          
    
    public void testCreatePojoAndCallSetterUpperString() {
        PropertySetterPojo pojo = new PropertySetterPojo();
        String testString = " ad adad asf 45      ";
        pojo.setProperty5(testString);
        String property5 = pojo.getProperty5();
        System.out.println("property5=" + property5);
        assertTrue(property5.equals(testString.toUpperCase().trim()));
    }
    
    public void testCreatePojoAndCallSetterDontTrimUpperString() {
        PropertySetterPojo pojo = new PropertySetterPojo();
        String testString = " GAStas 45      ";
        pojo.setProperty6(testString);
        String property6 = pojo.getProperty6();
        System.out.println("property6=" + property6);
        assertTrue(property6.equals(testString.toUpperCase()));
    }          
    
    
}

