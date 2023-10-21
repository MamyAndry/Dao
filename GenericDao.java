package dao;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.*;
import dao.helper.Helper;
//import etu2060.framework.FileUpload;

public class GenericDao{

//METHODS

    public static void save(Connection con,Object obj) throws Exception{
        boolean state = false;
        if(con == null){
            con = new DbConnection().connectToPostgres();
            state = true;
        }
        String query = "INSERT INTO " + Helper.getTableName(obj) + " VALUES(";
        String[] typeName = Helper.getTypeName(obj);
        Method[] getters = Helper.getGetters(obj);
        System.out.println("typename = " + typeName.length);
        for (int i = 0; i < typeName.length; i++) {
            System.out.println("typename = " + typeName[i]);
            if (typeName[i].equals("java.lang.String")) {
                query += "'" + getters[i].invoke(obj,(Object[]) null) + "'";
            } else if (typeName[i].equals("java.util.Date") || typeName[i].equals("java.sql.Date")) {
                query += "TO_DATE('" + getters[i].invoke(obj, (Object[]) null) + "','YYYY-MM-DD')";
            } else if (typeName[i].equals("java.time.LocalDateTime")) {
                query += "'" + getters[i].invoke(obj, (Object[]) null)+"'";
//            } else if (typeName[i].equals("etu2060.framework.FileUpload")){
//                FileUpload fu = (FileUpload) getters[i].invoke(obj, (Object[]) null);
//                query += fu.getName()+"'";
            }else{
                query += "" + getters[i].invoke(obj, (Object[]) null);
            }
            if(i < typeName.length-1){
                query += ",";
            }
        }
        query += ")";
//        System.out.println(query);
        Statement stmt =  con.createStatement();
        stmt.executeUpdate(query);
        if( state == true) con.close();
    }

    public static void update(Connection con,Object obj) throws Exception {
        boolean state = false;
        if(con == null){
            con = new DbConnection().connectToPostgres();
            state = true;
        }
        String id = Helper.getPKName(obj);
        String query = "UPDATE "+ Helper.getTableName(obj) +" SET ";
        String[] typeName = Helper.getTypeName(obj);
        String[] fields = Helper.getColumns(obj);
        Method[] getters = Helper.getGetters(obj);
        for (int i = 0; i < typeName.length; i++) {
            if (typeName[i].equals("java.lang.String")) {
                query += fields[i] + " = '" + getters[i].invoke(obj,(Object[]) null) + "'";
            } else if (typeName[i].equals("java.util.Date") || typeName[i].equals("java.sql.Date")) {
                query += fields[i] + " = TO_DATE('" + getters[i].invoke(obj, (Object[]) null) + "','YYYY-MM-DD')";
            } else if (typeName[i].equals("double")|| typeName[i].equals("int")) {
                query += fields[i] + " = " + getters[i].invoke(obj, (Object[]) null);
            } else if (typeName[i].equals("java.time.LocalDateTime")) {
                query += fields[i] + " = '" + getters[i].invoke(obj, (Object[]) null)+"'";
//            } else if (typeName[i].equals("etu2060.framework.FileUpload")){
//                FileUpload fu = (FileUpload) obj.getClass().getDeclaredMethod("get" + Helper.capitalize(fields[i])).invoke(obj, (Object[]) null);
//                query += fields[i] + " = '" + fu.getName()+"'";
            }
            if(i < typeName.length-1){
                query += ",";
            }
        }
        String condition = " WHERE " + id +" = '" + Helper.getPK(obj).invoke(obj, (Object[]) null)+"'";
        query += condition;
//        System.out.println(query);
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);
        if( state == true) con.close();
    }

    public static void delete(Connection con,Object obj) throws Exception {
        boolean state = false;
        if(con == null){
            con = new DbConnection().connectToPostgres();
            state = true;
        }
        String idTable = Helper.getPKName(obj);
        String tableName = Helper.getTableName(obj);
        String query = "DELETE FROM " + tableName ;
        String condition = " WHERE " + idTable  +" = '" + Helper.getPK(obj).invoke(obj, (Object[]) null)+"'";
        query += condition;
//        System.out.println(query);
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);
        if( state == true) con.close();
    }

    public static void deleteById(Connection con,Object obj,Object id) throws Exception {
        boolean state = false;
        if(con == null){
            con = new DbConnection().connectToPostgres();
            state = true;
        }
        String idTable = Helper.getPKName(obj);
        String tableName = Helper.getTableName(obj);
        String query = "DELETE FROM " + tableName ;
        String condition = " WHERE " + idTable  +" = '" + id +"'" ;
        query += condition;
        System.out.println(query);
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);
        if( state == true) con.close();
    }

    ///SELECT
    public static <T> List<T> findAll(Connection con,Object obj) throws Exception {
        boolean state = false;
        if(con == null){
            con = new DbConnection().connectToPostgres();
            state = true;
        }
        List<T> list = new ArrayList<T>();
        String query = "SELECT * FROM " + Helper.getTableName(obj);
//        System.out.println(query);
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        ResultSetMetaData rsmd = rs.getMetaData();
        while (rs.next()) {
            T temp = (T)obj.getClass().getDeclaredConstructor().newInstance((Object[]) null);
            List<Field> attribut = Helper.getColumnFields(temp);
            for (int col = 1; col <= rsmd.getColumnCount(); col++) {
                Class<?> fieldType = attribut.get(col-1).getType();
                System.out.println("fieldtypeS = " + fieldType);
                if(fieldType.getName().equals("java.time.LocalDateTime")){
                    temp.getClass().getDeclaredMethod("set" + Helper.capitalize(attribut.get(col-1).getName()) , fieldType ).invoke( temp , rs.getTimestamp(col).toLocalDateTime());
                }else{
                    Object args = fieldType.getDeclaredConstructor(String.class).newInstance(rs.getString(col));
                    temp.getClass().getDeclaredMethod("set" + Helper.capitalize(attribut.get(col-1).getName()) , fieldType ).invoke( temp , args );
                }
            }
            list.add(temp);
        }
        if( state == true) con.close();
        return list;

    }
    public static <T> List<T> findWhere(Connection con,Object obj,Object condition) throws Exception {
        boolean state = false;
        if(con == null){
            con = new DbConnection().connectToPostgres();
            state = true;
        }
        List<T> list = new ArrayList<T>();
        String query = "SELECT * FROM " + Helper.getTableName(obj) + " WHERE " + condition;
//        System.out.println(query);
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        ResultSetMetaData rsmd = rs.getMetaData();
        while (rs.next()) {
            T temp = (T)obj.getClass().getDeclaredConstructor().newInstance((Object[]) null);
            List<Field> attribut = Helper.getColumnFields(temp);
            for (int col = 1; col <= rsmd.getColumnCount(); col++) {
                Class<?> fieldType = attribut.get(col-1).getType();  
                if(fieldType.getName().equals("java.time.LocalDateTime")){
                    temp.getClass().getDeclaredMethod("set" + Helper.capitalize(attribut.get(col-1).getName()) , fieldType ).invoke( temp , rs.getTimestamp(col).toLocalDateTime());
                }else{
                    Object args = fieldType.getDeclaredConstructor(String.class).newInstance(rs.getString(col));
                    temp.getClass().getDeclaredMethod("set" + Helper.capitalize(attribut.get(col-1).getName()) , fieldType ).invoke( temp , args );
                }
            }
            list.add(temp);
        }
        if( state == true) con.close();
        return list;
    }

    public static <T> T findById(Connection con,Object obj,Object id) throws Exception {
        boolean state = false;
        if(con == null){
            con = new DbConnection().connectToPostgres();
            state = true;
        }
        String idTable = Helper.getPKName(obj);
        String tableName = Helper.getTableName(obj);
        String query = "SELECT * FROM " + tableName ;
        String condition = " WHERE " + idTable + " = '" + id + "'";
        query += condition;
//        System.out.println(query);
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        ResultSetMetaData rsmd = rs.getMetaData();
        rs.next();
        T temp = (T)obj.getClass().getDeclaredConstructor().newInstance((Object[]) null);
        List<Field> attribut = Helper.getColumnFields(temp);
        for (int col = 1; col <= rsmd.getColumnCount(); col++) {
            Class<?> fieldType = attribut.get(col-1).getType();
            if(fieldType.getName().equals("java.time.LocalDateTime")){
                temp.getClass().getDeclaredMethod("set" + Helper.capitalize(attribut.get(col-1).getName()) , fieldType ).invoke( temp , rs.getTimestamp(col).toLocalDateTime());
            }else{
                Object args = fieldType.getDeclaredConstructor(String.class).newInstance(rs.getString(col));
                temp.getClass().getDeclaredMethod("set" + Helper.capitalize(attribut.get(col-1).getName()) , fieldType ).invoke( temp , args );
            }
        }
        if( state == true) con.close();
        return temp;
    }

    ///OTHERS
    public static String fillZero(String num,int LgPK){ //Fill the zero Before the number
        int lim = (LgPK - 4) - num.length();
        String zero = ""+0;
        for(int i = 1 ; i <= lim ; i++){
            num = zero+num;
        }
        return num;
    }
    
    public static String constructPK(Connection con,String prefix,String sequenceName) throws Exception{ //Build The Primary Key in Form of String 
        boolean state = false;
        if(con == null){
            con = new DbConnection().connectToPostgres();
            state = true;
        }String res = prefix;
        String query = "SELECT nextval('" + sequenceName + "')";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        rs.next();
        String isa = fillZero(rs.getString(1) , 8);
        res += isa;
        if( state == true) con.close();
        return res;
    }
}
