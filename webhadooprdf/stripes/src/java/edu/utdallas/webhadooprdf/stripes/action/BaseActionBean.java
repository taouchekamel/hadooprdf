/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.utdallas.webhadooprdf.stripes.action;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;

/**
 *
 * @author hadoop
 */
public class BaseActionBean implements  ActionBean {

    public ActionBeanContext ctx;

    public void setContext(ActionBeanContext abc) {
        ctx=abc;
    }

    public ActionBeanContext getContext() {
        return ctx;
    }
}
