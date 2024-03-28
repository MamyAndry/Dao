/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dao.utils;

import com.dao.annotation.Table;
import com.dao.annotation.Column;
import com.dao.annotation.PrimaryKey;
import com.dao.annotation.ForeignKey;
import com.dao.annotation.GeneratedValue;
import com.dao.annotation.conf.ForeignType;
import com.dao.database.DbConnection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.sql.Date;
import java.sql.Time;
import java.sql.Statement;
import java.util.HashMap;
/**
 *
 * @author Mamisoa
 */
public class DaoUtility {
    
    public static void mergeTwoObject(Object o1, Object o2) throws Exception {
        Field[] fields = o2.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Method method = o2.getClass().getDeclaredMethod("get"+ObjectUtility.capitalize(fields[i].getName()));
            if(!ObjectUtility.isAtDefaultValue(method, o2)){
                fields[i].setAccessible(true);
                fields[i].set(o1, method.invoke(o2));
                fields[i].setAccessible(false);
            }
        }
    }
    
    public static String getFieldColumnName(Field field) {
        if(field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            if(!column.name().equals("")){
                return column.name();
            }
        }
        return field.getName();
    }

    public static String getConditionByAttributeValue(Object obj) throws Exception{
        String condition = " WHERE ";
        Field[] fields = obj.getClass().getDeclaredFields();
        List<Method> lst = getAllGettersMethod(obj);
        for (int i = 0; i < lst.size(); i++) {
            if(!ObjectUtility.isAtDefaultValue(lst.get(i), obj)){
                if(fields[i].isAnnotationPresent(Column.class)){
                    condition += getFieldColumnName(fields[i]) + " = '" + lst.get(i).invoke(obj) + "' AND ";
                }
            }
        }
        return condition.substring(0, condition.length() - 5);
    }
    
    //TABLE
    public static String getTableName(Object obj){
        if(obj.getClass().isAnnotationPresent(Table.class)){
            Table annotation = obj.getClass().getAnnotation(Table.class);
            if(!annotation.name().equals(""))return annotation.name();
        }
        String str = obj.getClass().getName();
        return str.split("\\.")[str.split("\\.").length - 1];
    }
    
    //COLUMN
    
    public static List<Field> getColumnFields(Class<?> objClass) throws Exception{
        List<Field> lst = new ArrayList<>();
        while(objClass != Object.class){
            for(Field declaredField : objClass.getDeclaredFields()) {
                if(declaredField.isAnnotationPresent(Column.class)){
                    // declaredField.setAccessible(true);
                    lst.add(declaredField);
                }else if(declaredField.isAnnotationPresent(ForeignKey.class)){
                    // declaredField.setAccessible(true);
                    lst.add(declaredField);
                }
            }
            objClass = objClass.getSuperclass();
        }
        return lst;
    }
    
    public static List<Field> getColumnFields( Class<?> objClass, Field[] fields) throws Exception{
        List<Field> lst = getColumnFields(objClass);
        for( Field l : lst ){
            for( Field f : fields ){
                if( l.getName().equals( f.getName() ) ){
                    // l.setAccessible(false);
                    break;
                }
            }
        }
        return lst;
    }   
    
    public static List<Field> getAllColumnFields(Object obj) throws Exception{
        return getColumnFields(obj.getClass());
    }
    
    public static List<Field> getAllColumnFields(Object obj, Field[] fields) throws Exception{
        return getColumnFields(obj.getClass(), fields);
    }
    
    public static String getName( Field field ){
        if( field.isAnnotationPresent(Column.class) && !field.getAnnotation(Column.class).name().isEmpty() )
            return field.getAnnotation(Column.class).name();
        // else if(field.isAnnotationPresent(PrimaryKey.class) && field.isAnnotationPresent(Column.class) && !field.getAnnotation(Column.class).name().isEmpty())
        //     return field.getAnnotation(Column.class).name();
        else if(field.isAnnotationPresent(ForeignKey.class) && !field.getAnnotation(ForeignKey.class).mappedBy().isEmpty())
            return field.getAnnotation(ForeignKey.class).mappedBy();
        return field.getName();
    }
    
    public static String[] getColumns(Object obj) throws Exception{
        List<Field> lst = getColumnFields(obj.getClass());
        ArrayList<String> list = new ArrayList<>();
        for(int i = 0; i < lst.size() ; i++){
            if(lst.get(i).isAnnotationPresent(Column.class)){
                Column col = lst.get(i).getAnnotation(Column.class);
                if(col.name().equals("")){
                    list.add(ObjectUtility.capitalize(lst.get(i).getName()));
                }else{
                    list.add(ObjectUtility.capitalize(col.name()));
                }
            }else if(lst.get(i).isAnnotationPresent(ForeignKey.class)){
                ForeignKey fk = lst.get(i).getAnnotation(ForeignKey.class);
                if(fk.foreignType() == ForeignType.OneToMany || fk.foreignType() == ForeignType.OneToOne){
                    list.add(ObjectUtility.capitalize(fk.mappedBy()));
                }
            }
        }
        return list.toArray(new String[0]);
    }

    public static List<String> getTableColumns(String tableName) throws Exception{
        Connection con = null;
        try{
            con = new DbConnection().connect();
            List<String> res = new ArrayList<>();
            String query = "SELECT * FROM "+tableName;
            
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            int count = rsmd.getColumnCount();
            for(int i = 1; i <= count; i++){
                res.add(rsmd.getColumnName(i));
            }
            return res;
        }finally{
            con.close();
        }
    }
    

    public static List<String> getTableColumns(Connection con, String tableName) throws Exception{
        List<String> res = new ArrayList<>();
        String query = "SELECT * FROM "+tableName;
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        ResultSetMetaData rsmd = rs.getMetaData();
        int count = rsmd.getColumnCount();
        for(int i = 1; i <= count; i++){
            res.add(rsmd.getColumnName(i));
        }
        return res;
    }
    
    public static String getListColumns(Object obj) throws Exception{
        String[] lst = getColumns(obj);
        String res = " ("; 
        for(String elt : lst){
            res += elt+",";
            // System.out.println(elt);
        }
        res = res.substring(0, res.lastIndexOf(','));
        return res+")";
    }
    
    //PRIMARY KEY
    public static String getPrimaryKeyName(Object obj) throws Exception{
        Field[] lst = obj.getClass().getDeclaredFields();
        for(Field elt : lst){
            if(elt.isAnnotationPresent(PrimaryKey.class) && elt.isAnnotationPresent(Column.class)  ){
                Column col = elt.getAnnotation(Column.class);
                if(col.name().equals(""))
                    return elt.getName();
                return col.name();
            }
        }
        return "";
    }
    public static String getPrimaryKeyColumnName(Object obj){
        Field[] lst = obj.getClass().getDeclaredFields();
        for(Field elt : lst){
            if(elt.isAnnotationPresent(Column.class) &&  elt.isAnnotationPresent(PrimaryKey.class)){
                Column col = elt.getAnnotation(Column.class);
                if(col.name().equals(""))
                    return elt.getName();
                return col.name();
            }
        }
        return "";
    }
    public static Method getPrimaryKeyGetMethod(Object obj) throws Exception{
        Field[] lst = obj.getClass().getDeclaredFields();
        for(Field elt : lst){
            if(elt.isAnnotationPresent(PrimaryKey.class))
               return obj.getClass().getDeclaredMethod("get" + ObjectUtility.capitalize(elt.getName()), (Class[]) null);
        }
        return null;
    }

    public static Method getPrimaryKeySetMethod(Object obj) throws Exception{
        Field[] lst = obj.getClass().getDeclaredFields();
        for(Field elt : lst){
            if(elt.isAnnotationPresent(PrimaryKey.class))
               return obj.getClass().getDeclaredMethod("set" + ObjectUtility.capitalize(elt.getName()), elt.getType());
        }
        return null;
    }
    public static Field getPrimaryKeyField(Object obj) throws Exception{
        Field[] lst = obj.getClass().getDeclaredFields();
        for(Field elt : lst){
            // System.out.println(elt);
            if(elt.isAnnotationPresent(PrimaryKey.class)){
                    elt.setAccessible(true);
                return elt;
            }
        }
        return null;
    }
    public static String[] getPrimaryKeyDetails(Object obj) throws Exception{
        String[] lst = new String[5];
        Field field = getPrimaryKeyField(obj);
        GeneratedValue pk = field.getAnnotation(GeneratedValue.class);
        String prefix = pk.prefix();
        lst[0] = ""+pk.autoIncrement();
        lst[1] = prefix;
        lst[2] = pk.sequence();
        lst[3] = ""+pk.length();
        lst[4] = ""+prefix.length();
        return lst;
    }

    //FOREIGN KEY
    public static String getForeignKeyName(Field field) throws Exception{
        ForeignKey fk = field.getAnnotation(ForeignKey.class);
        if(fk.mappedBy().equals(""))
            return field.getName();
        return fk.mappedBy();
    }

    public static Method getForeignKeyGetMethod(Object obj, Field field) throws Exception{
        return obj.getClass().getDeclaredMethod("get" + ObjectUtility.capitalize(field.getName()), (Class[]) null);
    }

    public static Method getForeignKeysetMethod(Object obj, Field field) throws Exception{
        return obj.getClass().getDeclaredMethod("set" + ObjectUtility.capitalize(field.getName()), field.getType());
    }

    //OTHERS (GETTERS AND SETTERS)
    public static List<Method> getGettersMethod(Class<?> objClass) throws Exception{
        List<Field> list = getColumnFields(objClass);
        List<Method> res = new ArrayList<>();
        for(Field field : list){
            if(field.getType().equals(boolean.class))
               res.add(objClass.getMethod("is" + ObjectUtility.capitalize(field.getName()),  (Class[])null)); 
            else{
                res.add(objClass.getMethod("get" + ObjectUtility.capitalize(field.getName()),  (Class[])null));
            }
        }
        return res;
    }

    public static Method getGetter(Object obj, Field field) throws Exception{
        if(field.getType().equals(boolean.class))
            return obj.getClass().getMethod("is" + ObjectUtility.capitalize(field.getName()),  (Class[])null); 
        else{
            return obj.getClass().getMethod("get" + ObjectUtility.capitalize(field.getName()),  (Class[])null);
        }
    } 
    public static Method getSetter(Object obj, Field field) throws Exception{
        return obj.getClass().getMethod("set" + ObjectUtility.capitalize(field.getName()), field.getType());
    } 

    public static List<Method> getAllGettersMethod(Object obj) throws Exception{
        Class<?> objClass = obj.getClass();
        List<Method> res = new ArrayList<>();
        res.add(getPrimaryKeyGetMethod(obj));
        res.addAll(getGettersMethod(objClass));
        objClass = objClass.getSuperclass();
        return res;
    }

    public static List<Method> getSettersMethod(Class<?> obj) throws Exception{
        List<Field> list = getColumnFields(obj);
        List<Method> res = new ArrayList<>();
        for(Field field : list)
            res.add(obj.getMethod("set" + ObjectUtility.capitalize(field.getName()), field.getType()));
        return res;
    
    }
    public static List<Method> getAllSettersMethod(Object obj) throws Exception{
        Class<?> objClass = obj.getClass();
        List<Method> res = new ArrayList<>();
        res.addAll(getSettersMethod(objClass));
        objClass = objClass.getSuperclass();
        return res;
    }
    
    public static int getColumnCount(ResultSet rs) throws Exception{
        ResultSetMetaData rsmd = rs.getMetaData();
        return rsmd.getColumnCount();
    }
    
    public static HashMap<String, Class<?>> getColumnNameAndType(ResultSet rs) throws Exception{
        HashMap<String, Class<?>> map = new HashMap<>();
        HashMap<Integer, Class<?>> mapping = ClassMapping.getClassMapTable();
        
        ResultSetMetaData rsmd = rs.getMetaData();
        int count = rsmd.getColumnCount();
        
        for(int i = 1; i <= count; i++){
            Integer key = rsmd.getColumnType(i);
            String column = rsmd.getColumnName(i);
            map.put(column, mapping.get(key));
        }
        return map;
    }   

    public static String convertForSql(Object attrb) throws Exception{
        if(attrb == null) return "null";
        Class<?> AttrClass = attrb.getClass();
        return AttrClass == Date.class ? "TO_DATE('"+attrb+"','YYYY-MM-DD')"
                : (AttrClass == Timestamp.class) ? "TO_TIMESTAMP('"+ attrb +"', 'YYYY-MM-DD HH24:MI:SS.FF')"
                : (AttrClass == String.class) || (AttrClass == Time.class) ? "'"+attrb+"'"
                : attrb.toString();
    }
}
