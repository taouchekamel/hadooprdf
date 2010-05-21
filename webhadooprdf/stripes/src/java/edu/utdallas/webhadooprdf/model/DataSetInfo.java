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
    private DataSet dataset;

    public DataSetInfo(String key, DataSet ds) {
        name=key;
        dataset=ds;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the dataset
     */
    public DataSet getDataset() {
        return dataset;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param dataset the dataset to set
     */
    public void setDataset(DataSet dataset) {
        this.dataset = dataset;
    }
}