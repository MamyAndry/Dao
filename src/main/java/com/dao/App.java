package com.dao;

import java.util.List;

import com.dao.database.DbConnection;

import com.dao.database.GenericDao;
import com.dao.database.Pageable;
import com.dao.file.JsonUtility;
import com.dao.test.entity.District;

public class App 
{
    public static void main( String[] args ){
        // DbConnection dbConnection = null;
        // try{
            // dbConnection = new DbConnection();
            // // dbConnection.init();
            // // dbConnection.setInUseConnection("DefaultConnection");
            // District tempDistrict = new District();
            // // Region region = new Region();
            // tempDistrict.setNomDistrict("hahA");
            // // region.setId(27);
            // // tempDistrictsetRegion(region);
            // Pageable pageable = new Pageable("ASC", 4, 2);
            // List<District> lst = GenericDao.findAll(dbConnection, tempDistrict, pageable); 
            // // List<District> lst = GenericDao.findAll(dbConnection, tempDistrict); 
            // for (District district : lst) {
            //     System.out.println(JsonUtility.encodeJson(district));
            // } 
            // tempDistrictsetId(158);
            // region.setNomRegion("hahA");
            // System.out.println(JsonUtility.encodeJson(district));
            // GenericDao.delete(dbConnection, district);
        // }catch(Exception e){
        //     e.printStackTrace();   
        // }finally{
        //     try {
        //         dbConnection.close();
        //     } catch (Exception ex) {
        //         // TODO: handle exception
        //         ex.printStackTrace();
        //     }
        // }
    }
}
