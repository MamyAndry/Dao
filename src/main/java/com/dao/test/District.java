package com.dao.test;

import com.dao.annotation.Column;
import com.dao.annotation.ForeignKey;
import com.dao.annotation.PrimaryKey;
import com.dao.annotation.Table;
import com.dao.annotation.conf.ForeignType;
import com.dao.database.BddObject;

@Table
public class District extends BddObject{
    @PrimaryKey(autoIncrement = true)
    @Column
    Integer id;
    @ForeignKey(mappedBy = "id_region", foreignType = ForeignType.OneToMany)
    Region region;
    @Column(name = "nom_district")
    String nomDistrict;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Region getRegion() {
        return region;
    }
    public void setRegion(Region region) {
        this.region = region;
    }
    public String getNomDistrict() {
        return nomDistrict;
    }
    public void setNomDistrict(String nomDistrict) {
        this.nomDistrict = nomDistrict;
    }
}
