/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdsframework.aspect.tests;

import org.cdsframework.aspect.aspects.PropertySetterInterface;
import org.cdsframework.aspect.annotations.PropertyListener;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cdsframework.util.LogUtils;

/**
 *
 * @author HLN Consulting, LLC
 */
public class PropertySetterPojo implements PropertySetterInterface {
    private static final LogUtils logger = LogUtils.getLogger(PropertySetterPojo.class);
    
    private String property1;
    private String property2;
    private String property3;
    private String property4;
    private String property5;
    private String property6;

    public String getProperty1() {
        return property1;
    }

    @PropertyListener(trackOldNewValue = true)
    public void setProperty1(String property1) {
        this.property1 = property1;
    }

    public String getProperty2() {
        return property2;
    }

    @PropertyListener
    public void setProperty2(String property2) {
        this.property2 = property2;
    }

    public String getProperty3() {
        return property3;
    }

    public void setProperty3(String property3) {
        propertyChanged("property3", property3, this.property3, false);
        this.property3 = property3;
    }
    
    public String getProperty4() {
        return property4;
    }

    // Keep from trimming strings
    @PropertyListener(trimStrings = false)
    public void setProperty4(String property4) {
        this.property4 = property4;
    }

    public String getProperty5() {
        return property5;
    }

    @PropertyListener(upperCase = true)
    public void setProperty5(String property5) {
        this.property5 = property5;
    }

    public String getProperty6() {
        return property6;
    }
    
    @PropertyListener(upperCase = true, trimStrings = false)
    public void setProperty6(String property6) {
        this.property6 = property6;
    }
    
    
    public void propertyChanged(String propertyName, Object newValue, Object oldValue, boolean trackOldNewValue) {
        String s = "1212";
        // do something
    }

    private final static Map<String, Field> propertyFieldMap = new HashMap<String, Field>();
    
    @Override
    public void propertyChanged(String propertyName, Object newValue, boolean trackOldNewValue) {
        final String METHODNAME = "propertyChanged ";
        Object oldValue;
        logger.logBegin(METHODNAME);
        try {
            Field field = propertyFieldMap.get(propertyName);
            if (field == null) {
                field = getClass().getDeclaredField(propertyName);
                propertyFieldMap.put(propertyName, field);
            }
            oldValue = field.get(this);
            propertyChanged(propertyName, newValue, oldValue, trackOldNewValue);            
            logger.info(METHODNAME, "propertyName=" + propertyName," oldValue=", oldValue, " newValue=", newValue, " trackOldNewValue=" , trackOldNewValue);
        } catch (Exception ex) {
            Logger.getLogger(PropertySetterPojo.class.getName()).log(Level.SEVERE, null, ex);
        }
        logger.logEnd(METHODNAME);

    }
}
