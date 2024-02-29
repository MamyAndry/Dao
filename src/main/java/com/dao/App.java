package com.dao;

// import java.util.List;

// import com.dao.annotation.Column;
// import com.dao.database.DbConnection;

// import java.lang.reflect.Field;
// import java.sql.Connection;
// import com.dao.database.GenericDao;
// import com.dao.file.JsonUtility;
// import com.dao.test.entity.District;
// import com.dao.test.entity.Region;
// import com.dao.utils.DaoUtility;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ){
        // DbConnection dbConnection = new DbConnection();
        // try{
        //     dbConnection.init();
        //     dbConnection.setInUseConnection("DefaultConnection");
        //     dbConnection = new DbConnection();
        //     District temp = new District();
        //     Region region = new Region();
            // temp.setNomDistrict("hahA");
            // region.setId(27);
            // temp.setRegion(region);
            // List<District> lst = GenericDao.findAll(dbConnection.getConnection(), temp); 
            // for (District district : lst) {
            //     System.out.println(JsonUtility.encodeJson(district));
            // } 
            // temp.setId(158);
            // region.setNomRegion("hahA");
            // System.out.println(JsonUtility.encodeJson(temp));
        //     GenericDao.delete(dbConnection.getConnection(), temp);
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
