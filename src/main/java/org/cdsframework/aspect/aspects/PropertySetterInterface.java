/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdsframework.aspect.aspects;

/**
 *
 * @author HLN Consulting, LLC
 */
public interface PropertySetterInterface {
    public void propertyChanged(String propertyName, Object newValue, boolean trackOldNewValue);
}
