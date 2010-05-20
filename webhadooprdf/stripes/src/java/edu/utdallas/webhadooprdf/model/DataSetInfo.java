/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.utdallas.webhadooprdf.model;

import edu.utdallas.hadooprdf.data.metadata.DataSet;

/**
 *
 * @author hadoop
 */
public class DataSetInfo {
    private String name;
    private DataSet dataSet;

    public DataSetInfo(String key, DataSet ds) {
        name=key;
        dataSet=ds;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the dataSet
     */
    public DataSet getDataSet() {
        return dataSet;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param dataSet the dataSet to set
     */
    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }
}