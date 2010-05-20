/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.utdallas.webhadooprdf.stripes.action;

import java.util.Date;
import java.util.Random;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

/**
 *
 * @author hadoop
 */
public class HelloActionBean extends BaseActionBean {

    private static final String VIEW="/WEB-INF/jsp/hello.jsp";
    private Date date;
    public Date getDate(){
        return date;
    }

    @DefaultHandler
    public Resolution currentDate(){
        date = new Date();
        return new ForwardResolution(VIEW);
    }

    public Resolution randomDate() {
        long max = System.currentTimeMillis();
        long random = new Random().nextLong() % max;
        date = new Date(random);
        return new ForwardResolution(VIEW);
    }

}
