package com.dao.test;

import com.dao.annotation.Column;
import com.dao.annotation.PrimaryKey;
import com.dao.annotation.Table;
import com.dao.database.BddObject;

@Table
public class Region extends BddObject{
    
    @PrimaryKey(autoIncrement = true)
    @Column
    Integer id;
    @Column(name = "nom_region")
    String nomRegion;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getNomRegion() {
        return nomRegion;
    }
    public void setNomRegion(String nomRegion) {
        this.nomRegion = nomRegion;
    }


}
