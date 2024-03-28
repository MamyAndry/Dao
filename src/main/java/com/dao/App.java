package com.dao;

import java.util.List;

import com.dao.database.DbConnection;

import com.dao.database.GenericDao;
import com.dao.database.Pageable;
import com.dao.file.JsonUtility;
import com.dao.test.entity.District;
import com.dao.test.entity.Region;

public class App 
{
    public static void main( String[] args ){
        DbConnection dbConnection = new DbConnection();
        try{
            dbConnection.init();
            dbConnection.setInUseConnection("DefaultConnection");
            District temp = new District();
            // Region region = new Region();
            // temp.setNomDistrict("hahA");
            // region.setId(27);
            // temp.setRegion(region);
            Pageable pageable = new Pageable("ASC", 4, 2);
            List<District> lst = GenericDao.findAll(dbConnection, temp, pageable); 
            for (District district : lst) {
                System.out.println(JsonUtility.encodeJson(district));
            } 
            // temp.setId(158);
            // region.setNomRegion("hahA");
            // System.out.println(JsonUtility.encodeJson(temp));
            // GenericDao.delete(dbConnection, temp);
        }catch(Exception e){
            e.printStackTrace();   
        }finally{
            try {
                dbConnection.close();
            } catch (Exception ex) {
                // TODO: handle exception
                ex.printStackTrace();
            }
        }
    }
}
