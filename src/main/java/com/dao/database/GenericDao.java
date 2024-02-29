/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dao.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import com.dao.annotation.ForeignKey;
import com.dao.annotation.conf.ForeignType;
import com.dao.utils.DaoUtility;
import com.dao.utils.ObjectUtility;

/**
 *
 * @author Mamisoa
 * @param <T>
 */
public class GenericDao{
    private static Field[] foreignSameFields;
    private static Object parent;


    public static Field[] getForeignSameFields() {
        return foreignSameFields;
    }

    public static void setForeignSameFields(Field[] foreignSameField) {
        foreignSameFields = foreignSameField;
    }

    public static Object getParent() {
        return parent;
    }

    public static void setParent(Object p) {
        parent = p;
    }

    //INSERT
    /**
     * 
     * @param con
     * @param name
     * @return
     * @throws Exception 
     */
    public static String getValues(Connection con,String name, Object obj) throws Exception{
        String values = "(";
        List<Field> lst = DaoUtility.getAllColumnFields(obj);
        for(Field field : lst){
            Method method = DaoUtility.getGetter(obj, field);
            Class<?> returnParam = method.getReturnType();
            if(method.equals(DaoUtility.getPrimaryKeyGetMethod(obj)) && method.invoke(obj, (Object[]) null) == null && returnParam.equals(String.class))
                values += DaoUtility.convertForSql(constructPK(con, name, obj)) + ", ";  
            else if(method.equals(DaoUtility.getPrimaryKeyGetMethod(obj)) && method.invoke(obj, (Object[]) null) == null && returnParam.equals(Integer.class))
                values += constructPK(con, name, obj) + ", ";
            else if(field.isAnnotationPresent(ForeignKey.class)){
                if(field.getAnnotation(ForeignKey.class).foreignType() == ForeignType.OneToMany || field.getAnnotation(ForeignKey.class).foreignType() == ForeignType.OneToOne){
                    Object temp = method.invoke(obj, (Object[]) null);
                    values += "'" + DaoUtility.getPrimaryKeyGetMethod(temp).invoke(temp, (Object[]) null) + "', ";
                }
            }
            else if(method.invoke(obj, (Object[]) null) == null){
                values += "default";
            }else{ 
                values += DaoUtility.convertForSql(method.invoke(obj, (Object[]) null)) + ", "; 
            }
            // System.out.println(values);
        }
        values = values.substring(0, values.lastIndexOf(','));
        values = values + ")";
        return values;
    }

    public static void save(Connection con, Object obj) throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            String query = "INSERT INTO " + DaoUtility.getTableName(obj) + DaoUtility.getListColumns(obj) + " VALUES " + getValues(con, "DefaultConnection", obj);
            System.out.println(query);
            PreparedStatement stmt =  con.prepareStatement(query);
            stmt.executeUpdate();
        }finally {
            if(state == true) con.close();
        }
    }
    
    
    public void save(Connection con,String name, Object obj) throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect(name);
                state = true;
            }
            String query = "INSERT INTO " + DaoUtility.getTableName(obj) + DaoUtility.getListColumns(obj)+" VALUES " + getValues(con, name, obj);
            System.out.println(query);
            PreparedStatement stmt =  con.prepareStatement(query);
            stmt.executeUpdate();
        }finally {
            if(state == true) con.close();
        }
    }
    //DELETE
    /**
     * 
     * @param con
     * @throws Exception 
     */
    public void deleteAll(Connection con, Object obj) throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            String query = "DELETE FROM " + DaoUtility.getTableName(obj);
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.executeUpdate();
        }finally {
            if(state == true) con.close();
        }
    }

    public static void delete(Connection con, Object obj) throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            String condition = DaoUtility.getPrimaryKeyName(obj) + " = '" + DaoUtility.getPrimaryKeyGetMethod(obj).invoke(obj, (Object[]) null) + "'";
            deleteWhere(con, condition, obj);
        }finally {
            if(state == true) con.close();
        }
    }
    public static void deleteWhere(Connection con, String condition, Object obj) throws Exception {
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            String query = "DELETE FROM " + DaoUtility.getTableName(obj) + " WHERE " + condition;
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.executeUpdate();
        }finally {
            if(state == true) con.close();
        }    
    }
    public static void deleteById(Connection con, Object id, Object obj) throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            String condition = DaoUtility.getPrimaryKeyName(obj)  +" = '" + id +"'";
            deleteWhere(con, condition, obj);
        }finally {
            if(state == true) con.close();
        }    
    }
    
    //UPDATE
    /**
     * 
     * @return
     * @throws Exception 
     */
    public static String updatedValues(Object obj) throws Exception{
        String query = "";
        List<Field> lst = DaoUtility.getAllColumnFields(obj);
        for(Field field : lst){
            Method method = DaoUtility.getGetter(obj, field);
            if(field.isAnnotationPresent(ForeignKey.class)){
                ForeignKey fk = field.getAnnotation(ForeignKey.class);
                if(fk.foreignType() == ForeignType.OneToMany || fk.foreignType() == ForeignType.OneToOne){
//                    BddObject temp = (BddObject) method.invoke(obj, (Object[]) null);
//                    values += "'" + DaoUtility.getPrimaryKeyGetMethod(temp).invoke(temp, (Object[]) null) + "', ";
                    BddObject temp = (BddObject) method.invoke(obj, (Object[]) null);
                    query += fk.mappedBy() + " = '" + DaoUtility.getPrimaryKeyGetMethod(temp).invoke(temp, (Object[]) null) + "', ";
                
                }
            }else
                query += DaoUtility.getName(field) + " = '" + method.invoke(obj, (Object[]) null) + "', ";
        }
        query = query.substring(0, query.lastIndexOf(','));
        return query;
    }
    public static void update(Connection con,Object obj) throws Exception {
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            String query = "UPDATE "+ DaoUtility.getTableName(obj) +" SET " + updatedValues(obj);
            query += " WHERE " + DaoUtility.getPrimaryKeyName(obj) +" = '" + DaoUtility.getPrimaryKeyGetMethod(obj).invoke( obj, (Object[]) null)+"'";
            // System.out.println(query);
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.executeUpdate();
        }finally{
            if(state == true) con.close();
        }      
    }
    //SELECT
    public static <T> List<T> findAll(Connection con, Object obj)throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            String query = "SELECT * FROM " + DaoUtility.getTableName(obj); 
            List<T> list = fetch(con, query, obj);
            return list;
        }finally {
            if(state == true) con.close();
        }    
    }
    
    public <T> List<T> findAllFromTable(Connection con, Object obj,String tableName)throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            String query = "SELECT * FROM " + tableName;
            List<T> list = fetch(con, query, obj);
            return list;
        }finally {
            if(state == true) con.close();
        }    
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T findById(Connection con, Object id, Object obj)throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            String query = "SELECT * FROM " + DaoUtility.getTableName(obj) + " WHERE " + DaoUtility.getPrimaryKeyName(obj) + " = '" + id + "'";
            // System.out.println(query);
            T temp = (T) fetch(con, query, obj).get(0);
            return temp;
        }finally {
            if(state == true) con.close();
        }    
    }
    
    // search line to print
    public static <T> List<T> findWhere(Connection con, Object obj) throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            
            String condition = DaoUtility.getConditionByAttributeValue(obj);
            String query = "SELECT * FROM " + DaoUtility.getTableName(obj) + condition;
            List<T> lst = fetch(con, query, obj);
            return lst;
        }finally{
            if( state == true) con.close();
        }
    }
    
    public static <T> List<T> findWhere(Connection con, String condition, Object obj) throws Exception {
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            String query = "SELECT * FROM " + DaoUtility.getTableName(obj) + " WHERE " + condition;
            List<T>  lst = fetch(con, query, obj);
            return lst;
        }finally {
            if(state == true) con.close();
        }    
    }
    
    //OTHERS
    public static void executeUpdate(Connection con, String query) throws Exception{
        boolean state = false;  
        try{      
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            Statement stmt =  con.createStatement();
            stmt.executeUpdate(query);
        }finally {
            if(state == true) con.close();
        }    
    }
    public static <T> List<T> executeQuery(Connection con, String query, Object obj) throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            List<T> list = new ArrayList<>();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            List<Field> fields = DaoUtility.getAllColumnFields(obj);
            List<Method> methods = DaoUtility.getAllSettersMethod(obj);
            List<String> columns = DaoUtility.getTableColumns(con, DaoUtility.getTableName(obj));
            while( rs.next() ){
                T now = convertToObject(con, rs, fields, methods, obj, columns);
                list.add(now);
            }
            return list;
        }finally {
            if(state == true) con.close();
        }    
    }
    
    public static <T> List<T> fetch(Connection con, String query, Object obj) throws Exception{
        List<T> list = new ArrayList<>();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        List<Field> fields = DaoUtility.getAllColumnFields(obj);
        List<Method> methods = DaoUtility.getAllSettersMethod(obj);
        List<String> columns = DaoUtility.getTableColumns(con, DaoUtility.getTableName(obj));
        while( rs.next() ){
            T now = convertToObject(con, rs, fields, methods, obj, columns);
            list.add(now);
        }
        return list;
    }
    
    @SuppressWarnings("unchecked")
    private static <T> T convertToObject(Connection con, ResultSet resultSet, List<Field> fields, List<Method> methods, Object obj, List<String> columns) throws Exception{
        Object object = obj.getClass().getDeclaredConstructor().newInstance();
        for (String column : columns) {
            for( int i = 0; i < fields.size() ; i++ ){
                if(DaoUtility.getName(fields.get(i)).equals(column)){   
                    Method method = methods.get(i);
                    Object value = resultSet.getObject(column);
                    if(fields.get(i).isAnnotationPresent(ForeignKey.class)){
                        value = treatForeignKey(con, value, fields.get(i), object);
                    }if(value == null){
                        value = ObjectUtility.getPrimitiveDefaultValue(fields.get(i).getType());
                    }
                    method.invoke(object, value);
                }
            }    
        }
        return (T) object;
    }
        
    public static String constructPK(Connection con, String name, Object obj)throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect(name);
                state = true;
            }
            String[] detail = DaoUtility.getPrimaryKeyDetails(obj);
            if(detail[0].equals("true"))
                return "default";
            String query = new DbConnection().getListConnection().get(name).getDatabaseType().getSequenceQuery();
            // query = "SELECT nextval('" + detail[2] + "')";
            query = query.replace("?", detail[2]);
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            String isa = ObjectUtility.fillZero(Integer.parseInt(detail[3]), Integer.parseInt(detail[4]), rs.getString(1));
            return detail[1]+isa;
        }finally {
                if(state == true) con.close();
        }
    }
    
    /**
     * 
     * @param con
     * @param value
     * @param field
     * @param object
     * @return
     * @throws Exception 
     */
    public static Object treatForeignKey(Connection con, Object value, Field field, Object object) throws Exception{
        Object temp = null;
        String classForName = "";
        if(field.getType() == java.util.List.class ){
            ParameterizedType listType = (ParameterizedType) field.getGenericType();
            Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
            String[] splited = listClass.toString().split(" ");
            classForName = splited[splited.length -1];
        }else{
            String[] splited = field.getType().toString().split(" ");
            classForName = splited[splited.length -1];
        }
        temp = Class.forName(classForName).getDeclaredConstructor().newInstance();
        Field[] tests = GenericDao.getSameFields( temp );
        for (Field test : tests) {
            if(test.getType() == value.getClass()){
                GenericDao.setParent(object);
            }
        }
        GenericDao.setForeignSameFields(tests);
        return createForeignKeyObject(con, field, temp, value, object);
    }
    /**
     * 
     * @param con
     * @param field
     * @param foreignKey
     * @param value
     * @param object
     * @return
     * @throws Exception 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Object createForeignKeyObject(Connection con, Field field, Object foreignKey, Object value, Object object) throws Exception{
        Object obj = null;
        if(field.getAnnotation(ForeignKey.class).foreignType() == ForeignType.OneToMany || field.getAnnotation(ForeignKey.class).foreignType() == ForeignType.OneToOne){
            if(GenericDao.getParent() != null){
                Method setter = DaoUtility.getSetter(obj, field);
                setter.invoke(obj, GenericDao.getParent());
            }else{
                obj = GenericDao.findById(con, value, foreignKey);
            }
        }
        else if(field.getAnnotation(ForeignKey.class).foreignType() == ForeignType.ManyToOne || field.getAnnotation(ForeignKey.class).foreignType() == ForeignType.ManyToMany){
            String condition = DaoUtility.getName(field) + " = '" + DaoUtility.getPrimaryKeyGetMethod(object).invoke(object, (Object[])null) + "'";
            obj = GenericDao.findWhere(con, condition, obj);
            ArrayList<Object> objects = (ArrayList) obj;
            for( Object o : objects ){
                for( Field f : GenericDao.getForeignSameFields() ){
                    Method setter = DaoUtility.getSetter(o, f);
                    setter.invoke(o, GenericDao.getParent());
                }
            }
        }
        return obj;
    }

    /**
     * 
     * @param field
     * @return
     * @throws Exception 
     */
    public static boolean sameClassExistence(Field field, Object obj) throws Exception{
        return field.isAnnotationPresent(ForeignKey.class) && field.getType() == obj.getClass();
    }
    /**
     * 
     * @param object
     * @return
     * @throws Exception 
     */
    public static Field[] getSameFields(Object object) throws Exception{
        Field[] fields = object.getClass().getDeclaredFields();
        List<Field> fs = new ArrayList<>();
        for( Field field : fields ){
            if( GenericDao.sameClassExistence(field, object) ){
                fs.add( field );
            }
        }
        return fs.toArray( new Field[0] );
    }

}
