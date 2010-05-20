/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.utdallas.webhadooprdf.stripes.action;

import edu.utdallas.hadooprdf.controller.HadoopRDF;
import edu.utdallas.hadooprdf.controller.HadoopRDFException;
import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.webhadooprdf.model.DataSetInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

/**
 *
 * @author hadoop
 */
public class BrowseDatasetActionBean extends BaseActionBean{

    private  static final String dsview="/WEB-INF/jsp/browse_dataset.jsp";
    private ArrayList<DataSetInfo> dataSets;
    private static HadoopRDF hrdf;

    @DefaultHandler
    public Resolution dataSetView(){
        return new ForwardResolution(dsview);
    }

    public List<DataSetInfo> getDatasets(){
        initDataSets();
        return dataSets;
    }
    private void initDataSets() {

    try {
        dataSets=new ArrayList<DataSetInfo>();
        hrdf = new HadoopRDF("conf/SemanticWebLabCluster", "/user/farhan/hadooprdf");
        Map<String,DataSet> ds=hrdf.getDataSetMap();
        Iterator iterator = ds.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,DataSet> entry = (Map.Entry<String,DataSet>)iterator.next();
            dataSets.add(new DataSetInfo(entry.getKey(),entry.getValue()));
         }

    } catch (HadoopRDFException ex) {
        Logger.getLogger(BrowseDatasetActionBean.class.getName()).log(Level.SEVERE, null, ex);
    }
          
   }

}
