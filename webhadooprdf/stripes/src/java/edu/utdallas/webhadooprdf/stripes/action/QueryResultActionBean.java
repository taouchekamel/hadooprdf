/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.utdallas.webhadooprdf.stripes.action;

import com.hp.hpl.jena.query.Dataset;
import edu.utdallas.webhadooprdf.model.DataSetInfo;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

/**
 *
 * @author Asif Mohammed
 */
public class QueryResultActionBean extends BaseActionBean {

    public static final String view="/WEB-INF/jsp/query_result.jsp";

    private DataSetInfo datasetInfo;

    public DataSetInfo getDatasetInfo() {
        return datasetInfo;
    }

    public void setDatasetInfo(DataSetInfo datasetinfo) {
        this.datasetInfo = datasetinfo;
    }

    @DefaultHandler
    public Resolution defaultView(){
       return new ForwardResolution(view);
    }



}
