package org.cdsframework.aspect.tests;

import org.cdsframework.aspect.annotations.RetryCallBack;
import org.cdsframework.aspect.annotations.RetryOnFailure;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.cdsframework.util.LogUtils;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author HLN Consulting LLC
 */
public class RetryPojo {
    
    private static final LogUtils logger = LogUtils.getLogger(RetryPojo.class);
    private int retryClassAttempts = 2;
    private long retryClassDelay = 1000;

    private List<Class<? extends Exception>> retryClassExceptions = new ArrayList<Class<? extends Exception>>();
    
    public RetryPojo() {
        retryClassExceptions.add(org.omg.CORBA.COMM_FAILURE.class);
        retryClassExceptions.add(org.omg.CORBA.OBJECT_NOT_EXIST.class);
        retryClassExceptions.add(javax.naming.NameNotFoundException.class);
        retryClassExceptions.add(java.net.ConnectException.class);
        retryClassExceptions.add(java.io.IOException.class);
    }

    @RetryOnFailure(attempts=5, exceptions={ArithmeticException.class, IllegalArgumentException.class}, delay=1000)
    public void doSomethingX() {
        long startTime = System.nanoTime();
        final String METHODNAME = "doSomethingX ";
        try {
            logger.info(METHODNAME, "working ");
            Thread.sleep(500);
            logger.info(METHODNAME, "cause a divide by zero ");
            // Cause a devide by zero
            int divideByZero = 2/0;
            
        } catch (InterruptedException ex) {
            logger.error(METHODNAME, "An InterruptedException has occurred; Message: ", ex.getMessage(), ex);
        }
    }

    @RetryOnFailure(attempts=5, exceptions={ArithmeticException.class, IllegalArgumentException.class}, delay=3000)
    public void doSomethingY() {
        long startTime = System.nanoTime();
        final String METHODNAME = "doSomethingY ";
        try {
            logger.info(METHODNAME, "working ");
            Thread.sleep(500);
            logger.info(METHODNAME, "cause a divide by zero");
            // Cause a devide by zero
            int divideByZero = 2/0;

        } catch (ArithmeticException ex) {
            logger.error(METHODNAME, "An ArithmeticException has occurred; Message: ", ex.getMessage(), ex);
            // Throw Class Exception
            org.omg.CORBA.COMM_FAILURE commFailure = new org.omg.CORBA.COMM_FAILURE();
            throw commFailure;
            
        } catch (InterruptedException ex) {
            logger.error(METHODNAME, "An InterruptedException has occurred; Message: ", ex.getMessage(), ex);
        }
    }

    @RetryOnFailure(exceptions={ArithmeticException.class, IllegalArgumentException.class})
    public void doSomethingZ() {
        long startTime = System.nanoTime();
        final String METHODNAME = "doSomethingZ ";
        try {
            logger.info(METHODNAME, "working ");
            Thread.sleep(500);
            logger.info(METHODNAME, "cause a divide by zero");
            // Cause a devide by zero
            int divideByZero = 2/0;
            
        } catch (InterruptedException ex) {
            logger.error(METHODNAME,"An InterruptedException has occurred; Message: ", ex.getMessage(), ex);
        }
    }
    
    
    @RetryOnFailure(attempts=5, exceptions={ArithmeticException.class, IllegalArgumentException.class}, delay=1000)
    public void doSomething(int count) {
        long startTime = System.nanoTime();
        final String METHODNAME = "doSomething ";
        for (int i = 0; i < count; i++) {
            try {
                logger.info(METHODNAME, "working ", i );
                Thread.sleep(500);
                if (i == 1) {
                    logger.info(METHODNAME, "cause a divide by zero");
                    // Cause a devide by zero
                    int divideByZero = 2/0;
                }
                else if (i == 9) {
                    logger.info(METHODNAME, "cause a null pointer");
                    // Cause a devide by zero
                    String x = null;
                    String toLowerCase = x.toLowerCase();
                }
                
            } catch (InterruptedException ex) {
                logger.error(METHODNAME, "An InterruptedException has occurred; Message: ", ex.getMessage(), ex);
            }
        }
    }

    
    @RetryCallBack
    public void retryCallBack(Method retryMethod, Exception exception) {
        final String METHODNAME = "retryCallBack ";
        logger.info(METHODNAME, "retryMethod=", retryMethod.getName(), " exception=", exception.getClass().getName());
    }

//    public int getAttempts() {
//        return attempts;
//    }
//
//    public void setAttempts(int attempts) {
//        this.attempts = attempts;
//    }
    
    
}
