/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdsframework.aspect.aspects;

import org.cdsframework.aspect.annotations.PropertyListener;
import java.lang.reflect.Method;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;

/**
 *
 * @author HLN Consulting, LLC
 */
@Aspect
public class PropertySetter {

    // When all classes that implement PropertySetterInterface are cleaned up where the annotation is not necessary
    // remove the @annotation constraint and all setters in the classes will execute the propertySetter
    // remove the anotation logic below as well
    @Around("execution(* set*(..)) && target(org.cdsframework.aspect.aspects.PropertySetterInterface) && @annotation(org.cdsframework.aspect.annotations.PropertyListener)")    
    public Object propertySetter(ProceedingJoinPoint point) throws Throwable {
        Signature signature = point.getSignature();
        Method method = ((MethodSignature) signature).getMethod();
        PropertyListener propertyChange = method.getAnnotation(PropertyListener.class);
        boolean trackOldNewValue = propertyChange.trackOldNewValue();
        boolean trim = propertyChange.trimStrings();
        boolean upperCase = propertyChange.upperCase();
        boolean truncate = propertyChange.truncate() > 0;
        int truncateLength = propertyChange.truncate();        
        PropertySetterInterface target = (PropertySetterInterface) point.getTarget();
        String propertyName = signature.getName().substring(3);
        propertyName = propertyName.substring(0,1).toLowerCase() + propertyName.substring(1);
        
        Object[] args = point.getArgs();
        for (int i = 0; i < args.length; i++) {
            Object objectValue = args[i];
            if (objectValue != null && objectValue instanceof String) {
                String stringValue = (String) objectValue;
                if (stringValue.isEmpty()) {
                    args[i] = null;
                }
                else {
                    String trimmedValue = stringValue.trim();
                    if (trimmedValue.isEmpty()) {
                        args[i] = null;
                    }
                    else { 
                        if (trim) {
                            args[i] = trimmedValue;
                        }
                        if (upperCase) {
                            args[i] = ((String)args[i]).toUpperCase();
                        }
                        if (truncate) {
                            String stringToTruncate = ((String) args[i]);
                            if (stringToTruncate.length() > truncateLength) {
                                args[i] = stringToTruncate.substring(0, truncateLength);
                            }
                        }
                    }
                    
                }
            }
        }
        target.propertyChanged(propertyName, args[0], trackOldNewValue);
        return point.proceed(args);
    }
}
