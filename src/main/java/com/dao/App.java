package com.dao;

import java.util.List;

import com.dao.database.DbConnection;
import com.dao.database.GenericDao;
import com.dao.file.JsonUtility;
import com.dao.test.entity.District;
import com.dao.utils.ObjectUtility;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        DbConnection dbConnection = new DbConnection();
        try{
            dbConnection.init();
            dbConnection.setInUseConnection("DefaultConnection");
            // System.out.println(dbConnection.getConnection());
            District temp = new District();
            List<District> lst = GenericDao.findAll(dbConnection.getConnection(), temp); 
            for (District district : lst) {
                System.out.println(JsonUtility.encodeJson(district));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                dbConnection.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
