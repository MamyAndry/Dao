package dao;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.*;
import dao.helper.Helper;
//import etu2060.framework.FileUpload;

public class BddObject{
    String prefix;
    String sequenceName;
    int lenght;

//GETTERS
    public String getPrefix() {
        return prefix;
    }
    public int getLenght() {
        return lenght;
    }
    public String getSequenceName() {
        return sequenceName;
    }
//SETTERS
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    public void setLenght(int lenght) {
        this.lenght = lenght;
    }
    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

//METHODS

    public void save(Connection con) throws Exception{
        boolean state = false;
        if(con == null){
            con = new DbConnection().connection();
            state = true;
        }
        String query = "INSERT INTO " + Helper.getTableName( this ) + " VALUES(";
        String[] typeName = Helper.getTypeName( this );
        Method[] getters = Helper.getGetters( this );
        // System.out.println("typename = " + typeName.length);
        for (int i = 0; i < typeName.length; i++) {
            // System.out.println("typename = " + typeName[i]);
            if (typeName[i].equals("java.lang.String")) {
                query += "'" + getters[i].invoke( this , (Object[]) null) + "'";
            } else if (typeName[i].equals("java.util.Date") || typeName[i].equals("java.sql.Date")) {
                query += "TO_DATE('" + getters[i].invoke( this , (Object[]) null) + "','YYYY-MM-DD')";
            } else if (typeName[i].equals("java.time.LocalDateTime")) {
                if(getters[i].invoke( this , (Object[]) null) == null){
                    query += getters[i].invoke( this , (Object[]) null);
                }
                else{
                    query += "'" + getters[i].invoke( this , (Object[]) null)+"'";
                }
//            } else if (typeName[i].equals("etu2060.framework.FileUpload")){
//                FileUpload fu = (FileUpload) getters[i].invoke( this , (Object[]) null);
//                query += "'"+fu.getName()+"'";
            }else{
                query += "" + getters[i].invoke( this , (Object[]) null);
            }
            if(i < typeName.length-1){  
                query += ",";
            }
        }
        query += ")";
        System.out.println(query);
        Statement stmt =  con.createStatement();
        stmt.executeUpdate(query);
        if( state == true) con.close();
    }
    
    public void executeQuery(Connection con, String query) throws Exception {
        boolean state = false;
        if(con == null){
            con = new DbConnection().connection();
            state = true;
        }
        Statement stmt =  con.createStatement();
        stmt.executeUpdate(query);
        if( state == true) con.close();
    }
    
    public void update(Connection con) throws Exception {
        boolean state = false;
        if(con == null){
            con = new DbConnection().connection();
            state = true;
        }
        String id = Helper.getPKName(this);
        String query = "UPDATE "+ Helper.getTableName(this) +" SET ";
        String[] typeName = Helper.getTypeName(this);
        String[] fields = Helper.getColumns(this);
        Method[] getters = Helper.getGetters(this);
        for (int i = 0; i < typeName.length; i++) {
            if (typeName[i].equals("java.lang.String")) {
                query += fields[i] + " = '" + getters[i].invoke(this,(Object[]) null) + "'";
            } else if (typeName[i].equals("java.util.Date") || typeName[i].equals("java.sql.Date")) {
                query += fields[i] + " = TO_DATE('" + getters[i].invoke( this , (Object[]) null) + "','YYYY-MM-DD')";
            } else if (typeName[i].equals("double")|| typeName[i].equals("int")) {
                query += fields[i] + " = " + getters[i].invoke( this , (Object[]) null);
            } else if (typeName[i].equals("java.time.LocalDateTime")) {
                query += fields[i] + " = '" + getters[i].invoke( this , (Object[]) null)+"'";
//            } else if (typeName[i].equals("etu2060.framework.FileUpload")){
//                FileUpload fu = (FileUpload) this.getClass().getDeclaredMethod("get" + Helper.capitalize(fields[i])).invoke(this, (Object[]) null);
//                query += fields[i] + " = '" + fu.getName()+"'";
            }
            if(i < typeName.length-1){
                query += ",";
            }
        }
        String condition = " WHERE " + id +" = '" + Helper.getPK(this).invoke( this, (Object[]) null)+"'";
        query += condition;
        System.out.println(query);
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);
        if( state == true) con.close();
    }

    public void delete(Connection con) throws Exception {
        boolean state = false;
        if(con == null){
            con = new DbConnection().connection();
            state = true;
        }
        String idTable = Helper.getPKName(this);
        String tableName = Helper.getTableName(this);
        String query = "DELETE FROM " + tableName ;
        String condition = " WHERE " + idTable  +" = '" + Helper.getPK(this).invoke(this, (Object[]) null)+"'";
        query += condition;
        System.out.println(query);
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);
        if( state == true) con.close();
    }

    public void deleteById(Connection con, Object id) throws Exception {
        boolean state = false;
        if(con == null){
            con = new DbConnection().connection();
            state = true;
        }
        String idTable = Helper.getPKName(this);
        String tableName = Helper.getTableName(this);
        String query = "DELETE FROM " + tableName ;
        String condition = " WHERE " + idTable  +" = '" + id +"'" ;
        query += condition;
        System.out.println(query);
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);
        if( state == true) con.close();
    }
        public void deleteWhere(Connection con, String condition) throws Exception {
        boolean state = false;
        if(con == null){
            con = new DbConnection().connection();
            state = true;
        }
        String tableName = Helper.getTableName(this);
        String query = "DELETE FROM " + tableName;
        query += " WHERE " + condition;
        System.out.println(query);
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);
        if( state == true) con.close();
    }

    ///SELECT
    public <T> List<T> findAll(Connection con) throws Exception {
        boolean state = false;
        if(con == null){
            con = new DbConnection().connection();
            System.out.println(con);
            state = true;
        }
        List<T> list = new ArrayList<T>();
        String query = "SELECT * FROM " + Helper.getTableName(this);
//        System.out.println(query);
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        ResultSetMetaData rsmd = rs.getMetaData();
        while (rs.next()) {
            T temp = (T)this.getClass().getDeclaredConstructor().newInstance((Object[]) null);
            List<Field> attribut = Helper.getColumnFields(temp);
            for (int col = 1; col <= rsmd.getColumnCount(); col++) {
                Class<?> fieldType = attribut.get(col-1).getType();
                if(fieldType.getName().equals("java.time.LocalDateTime")){
                    Timestamp tempo = rs.getTimestamp(col);
                    if( tempo != null){
                        temp.getClass().getDeclaredMethod("set" + Helper.capitalize(attribut.get(col-1).getName()) , fieldType ).invoke( temp , tempo.toLocalDateTime());
                    }
                }else if(fieldType.getName().equals("java.sql.Date")){
                    temp.getClass().getDeclaredMethod("set" + Helper.capitalize(attribut.get(col-1).getName()) , fieldType ).invoke( temp , Date.valueOf(rs.getString(col)));
//                }else if(fieldType.getName().equals("etu2060.framework.FileUpload")){
//                    FileUpload fu = new FileUpload();
//                    fu.setName(rs.getString(col));
//                    temp.getClass().getDeclaredMethod("set" + Helper.capitalize(attribut.get(col-1).getName()) , fieldType ).invoke( temp , fu);
                }
                else{
                    Object args = fieldType.getDeclaredConstructor(String.class).newInstance(rs.getString(col));
                    System.out.println(args);
                    temp.getClass().getDeclaredMethod("set" + Helper.capitalize(attribut.get(col-1).getName()) , fieldType ).invoke( temp , args );
                }
            }
            list.add(temp);
        }
        if( state == true) con.close();
        return list;

    }
    public <T> List<T> findWhere(Connection con,Object condition) throws Exception {
        boolean state = false;
        if(con == null){
            con = new DbConnection().connection();
            state = true;
        }
        List<T> list = new ArrayList<T>();
        String query = "SELECT * FROM " + Helper.getTableName(this) + " WHERE " + condition;
//        System.out.println(query);
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        ResultSetMetaData rsmd = rs.getMetaData();
        while (rs.next()) {
            T temp = (T)this.getClass().getDeclaredConstructor().newInstance((Object[]) null);
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

    public <T> T findById(Connection con,Object id) throws Exception {
        boolean state = false;
        if(con == null){
            con = new DbConnection().connection();
            state = true;
        }
        String idTable = Helper.getPKName(this);
        String tableName = Helper.getTableName(this);
        String query = "SELECT * FROM " + tableName ;
        String condition = " WHERE " + idTable + " = '" + id + "'";
        query += condition;
//        System.out.println(query);
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        ResultSetMetaData rsmd = rs.getMetaData();
        rs.next();
        T temp = (T)this.getClass().getDeclaredConstructor().newInstance((Object[]) null);
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
    public String fillZero(String num){ //Fill the zero Before the number
        int lim = (this.getLenght() - 4) - num.length();
        String zero = ""+0;
        for(int i = 1 ; i <= lim ; i++){
            num = zero+num;
        }
        return num;
    }
    
    public String constructPK(Connection con) throws Exception{ //Build The Primary Key in Form of String 
        boolean state = false;
        if(con == null){
            con = new DbConnection().connectToPostgres();
            state = true;
        }
        String res = this.getPrefix();
        String query = "SELECT nextval('" + this.getSequenceName() + "')";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        rs.next();
        String isa = fillZero(rs.getString(1));
        res += isa;
        if( state == true) con.close();
        return res;
    }
}
