/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdsframework.aspect.aspects;

import java.lang.reflect.Method;
import java.util.Stack;
import org.apache.log4j.LogManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.cdsframework.util.LogUtils;

/**
 *
 * @author HLN Consulting LLC
 */

@Aspect
public abstract class Trace {
    private static final LogUtils logger = LogUtils.getLogger(Trace.class);
    private long startTime;
    Stack<TraceCall> traceCalls = new Stack<TraceCall>();
    
    public @Pointcut abstract void traceCall();
    
    @Before("traceCall()")
    public void traceBegin(JoinPoint joinPoint) {
        startTime = System.nanoTime();
        String sourceName = joinPoint.getSourceLocation().getWithinType().getCanonicalName();
        traceCalls.push(new TraceCall(sourceName, startTime));
        Signature signature = joinPoint.getSignature();
        String line = "" + joinPoint.getSourceLocation().getLine();
        logger.info("BEGIN from " + sourceName + " line " + line + " to " +signature.getDeclaringTypeName() + "." + signature.getName());
    }            

    @After("traceCall()")
    public void traceEnd(JoinPoint joinPoint) {
        long endTime = System.nanoTime();
        TraceCall traceCall = traceCalls.pop();
        Signature sig = joinPoint.getSignature();
        String line =""+ joinPoint.getSourceLocation().getLine();
        String sourceName = joinPoint.getSourceLocation().getWithinType().getCanonicalName();
        logger.info("End from " + sourceName + " line " + line + " to " +sig.getDeclaringTypeName() + "." + sig.getName() + 
                " duration(ms): " + (endTime - traceCall.startTime)/1000000.0);
    }            
    
    private class TraceCall {
        public TraceCall(String source, long startTime) {
            this.source = source;
            this.startTime = startTime;
        }
        private String source;
        private long startTime;
        
    }
}