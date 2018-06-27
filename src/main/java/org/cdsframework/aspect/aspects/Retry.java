/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdsframework.aspect.aspects;

import org.cdsframework.aspect.annotations.RetryCallBack;
import org.cdsframework.aspect.annotations.RetryOnFailure;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.cdsframework.util.LogUtils;

/**
 *
 * @author HLN Consulting LLC
 */
@Aspect
public class Retry { 
    private static final LogUtils logger = LogUtils.getLogger(Retry.class);

    @Around("@annotation(org.cdsframework.aspect.annotations.RetryOnFailure) && !cflow(within(org.cdsframework.aspect.aspects.Retry))")    
    public Object retry(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        final String METHODNAME = "retry ";

        Method retryMethod = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
        RetryOnFailure retryOnFailureAnnotation = retryMethod.getAnnotation(RetryOnFailure.class);
        Object targetObject = proceedingJoinPoint.getTarget();
        if (logger.isDebugEnabled()) {
            logger.debug(METHODNAME + "targetObject.getClass().getCanonicalName()=" + targetObject.getClass().getCanonicalName());
        }

        // Get the attempts/delay parameter (Use Class if Method not set, or Method if Method not default) 
        int attempts = getAttempts(retryMethod, retryOnFailureAnnotation, targetObject);
        long delay = getDelay(retryMethod, retryOnFailureAnnotation, targetObject);
        
        // Get the Annotated Retry Method Exceptions
        List<Class<? extends Exception>> retryMethodExceptions = new ArrayList<Class<? extends Exception>>();
        if (retryOnFailureAnnotation.exceptions() != null) {
            retryMethodExceptions = Arrays.asList(retryOnFailureAnnotation.exceptions());
        }

        logger.debug(METHODNAME + "retryMethodExceptions.size()=" + retryMethodExceptions.size());
        
        // Get the Class Retry Exceptions
        List<Class<? extends Exception>> retryClassExceptions = new ArrayList<Class<? extends Exception>>();         
        try {
            
            Field field = targetObject.getClass().getDeclaredField("retryClassExceptions");
            field.setAccessible(true);
            if (field.get(targetObject) != null) {
                retryClassExceptions = (List<Class<? extends Exception>>) field.get(targetObject);
                if (logger.isDebugEnabled()) {
                    logger.debug(METHODNAME + "retryClassExceptions.size()=" + retryClassExceptions.size());
                    for (Class<? extends Exception> retryClassException : retryClassExceptions) {
                        logger.debug(METHODNAME + "retryClassException=" + retryClassException.getCanonicalName());
                    }
                }
            }
        }
        catch (NoSuchFieldException e) {
            // This is ok
        }

        logger.debug(METHODNAME + "retryClassExceptions.size()=" + retryClassExceptions.size());
        
        // Get the Thrown Exceptions not in the Retry Exceptions
        List<Class<?>> thrownExceptions = new ArrayList<Class<?>>();
        Class<?>[] exceptionTypes = retryMethod.getExceptionTypes();
        for (Class<?> thrownException : exceptionTypes) {
            if (!retryMethodExceptions.contains(thrownException)) {
                thrownExceptions.add(thrownException);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug(METHODNAME + "Retry Method Exceptions " + retryMethodExceptions.toString());
            logger.debug(METHODNAME + "Retry Class Exceptions " + retryClassExceptions.toString());
            logger.debug(METHODNAME + "Thrown Exceptions " + thrownExceptions.toString());
        }

        Throwable rootCause = null;
        Method retryCallBackMethod = null;
        for (int i = 0; i <= attempts; i++) {
            try {
                if (i != 0) {
                    logger.info(METHODNAME + "calling " +  retryMethod.getName() + " attempt " + (i + 1) + " of " + attempts);
                }                
                return proceedingJoinPoint.proceed();
            } catch (Throwable e) {
                rootCause = e;
                if (ExceptionUtils.getRootCause(e) != null) {
                    rootCause = ExceptionUtils.getRootCause(e);
                }
                logger.debug(METHODNAME + "rootCause: " + rootCause.getClass().getName() + " for: " + e.getClass().getName());
                
                // Retry the Exception ?
                if (retryMethodExceptions.contains(rootCause.getClass()) || retryClassExceptions.contains(rootCause.getClass())) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(METHODNAME + "Exception found within retriable exceptions, will retry");
                        logger.debug(METHODNAME + (i + 1) + " >= " + attempts + " == " + ((i + 1) >= attempts));
                    }
//                  if (logger.isDebugEnabled()) {
                        logger.info(METHODNAME + "Exception is retryable, rootCause: " + rootCause.getClass().getName() + " sleeping for " + delay);
//                  }
                    Thread.sleep(delay);
                    if (retryCallBackMethod == null) {
                        retryCallBackMethod = getRetryCallBackMethod(targetObject);
                    }
                    if (retryCallBackMethod != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug(METHODNAME + "retryCallBackMethod=" + retryCallBackMethod.getName());
                            logger.debug(METHODNAME + "retryMethod=" + retryMethod.getName());
                        }
                        retryCallBackMethod.invoke(targetObject, retryMethod, rootCause);
                    }
                    if ((i + 1) >= attempts) {
                        logger.info(METHODNAME + "attempts " + attempts + " exhausted");
                        break;
                    }
                }
                else {
                    if (logger.isDebugEnabled()) {
                        if (thrownExceptions.contains(rootCause.getClass())) {
                            logger.debug(METHODNAME + "Exception found within thrownExceptions, will not retry");
                        }
                        else {
                            logger.debug(METHODNAME + "Exception not within retryExceptions/thrownExceptions, will not retry");
                        }
                    }
                    throw rootCause;
                }
            }
        }
        if (rootCause != null) {
            if (logger.isDebugEnabled()) {
                logger.debug(METHODNAME + "exhausted all attempts, throwing exception " + rootCause.getClass().getName());
            }
        }
        throw rootCause;
    }
    
    private int getAttempts(Method retryMethod, RetryOnFailure retryOnFailureAnnotation, Object targetObject) 
            throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException {
        final String METHODNAME = "getAttempts ";
        int methodAttempts = retryOnFailureAnnotation.attempts();
        if (logger.isDebugEnabled()) {
            logger.debug(METHODNAME + "targetObject.getClass().getCanonicalName()=" + targetObject.getClass().getCanonicalName());
        }
        
        // Get Class Attempts value, which overrides method annotation value
        Integer classAttempts = null;
        try {
            Field field = targetObject.getClass().getDeclaredField("retryClassAttempts");
            field.setAccessible(true);
            classAttempts = field.getInt(targetObject);
        }
        catch (NoSuchFieldException e) {
            // This is ok
        }
        
        int defaultAttempts = (Integer) RetryOnFailure.class.getMethod("attempts").getDefaultValue();
        if (logger.isDebugEnabled()) {
            logger.debug(METHODNAME + "classAttempts=" + classAttempts + 
                    " methodAttempts=" + methodAttempts + 
                    " defaultAttempts=" + defaultAttempts);
        }
        
        // Always use method attempts
        int attempts = methodAttempts;
        
        // Class overrides Method if Method is not set?
        if (classAttempts != null && methodAttempts == defaultAttempts) {
            attempts = classAttempts.intValue();
        }

        logger.debug(METHODNAME + "attempts=" + attempts);
        
        return attempts;
    }

    private long getDelay(Method retryMethod, RetryOnFailure retryOnFailureAnnotation, Object targetObject) 
            throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException {
        final String METHODNAME = "getDelay ";
        long methodDelay = retryOnFailureAnnotation.delay();
        if (logger.isDebugEnabled()) {
            logger.debug(METHODNAME + "targetObject.getClass().getCanonicalName()=" + targetObject.getClass().getCanonicalName());
        }
        
        // Get Class Delay value, which overrides method annotation value
        Long classDelay = null;
        try {
            Field field = targetObject.getClass().getDeclaredField("retryClassDelay");
            field.setAccessible(true);
            classDelay = field.getLong(targetObject);
        }
        catch (NoSuchFieldException e) {
            // This is ok
        }
        
        long defaultDelay = (Long) RetryOnFailure.class.getMethod("delay").getDefaultValue();
        if (logger.isDebugEnabled()) {
            logger.debug(METHODNAME + "classDelay=" + classDelay + 
                    " methodDelay=" + methodDelay + 
                    " defaultDelay=" + defaultDelay);
        }
        
        // Always use method attempts
        long delay = methodDelay;
        
        // Class overrides Method if Method is not set?
        if (classDelay != null && methodDelay == defaultDelay) {
            delay = classDelay.longValue();
        }

        logger.debug(METHODNAME + "delay=" + delay);
        
        return delay;
    }
    
    private Method getRetryCallBackMethod(Object targetObject) {
        Method retryCallBackMethod = null;
        final String METHODNAME = "getRetryCallBackMethod ";
        for (Method declaredMethod : targetObject.getClass().getDeclaredMethods()) {
            if (logger.isDebugEnabled()) {
                logger.debug(METHODNAME + "declaredMethod.getName()=" + declaredMethod.getName());
            }
            // Public = declaredMethod.getModifiers() == 1
            if (declaredMethod.getAnnotation(RetryCallBack.class) != null && declaredMethod.getModifiers() == 1) {
                Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
                if (parameterTypes != null && parameterTypes.length == 2) {
                    // Check for Method and Exception parameters
                    if (parameterTypes[0] == Method.class && parameterTypes[1] == Exception.class) {
                        retryCallBackMethod = declaredMethod;
                        break;
                    }
                }
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug(METHODNAME + "retryCallBackMethod=" + retryCallBackMethod);
        }

        return retryCallBackMethod;
    }
    
}