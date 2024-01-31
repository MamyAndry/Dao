/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dao.database;

import com.dao.annotation.ForeignKey;
import com.dao.annotation.conf.ForeignType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import java.util.ArrayList;
import java.util.List;

import com.dao.utils.DaoUtility;
import com.dao.utils.ObjectUtility;

/**
 *
 * @author Mamisoa
 * @param <T>
 */ 
public class BddObject  {
    private String tableName;
    private String primaryKeyName;
    private Field[] foreignSameFields;
    private Object parent;

    //GETTERS & SETTERS
    public String getTableName(){
        return this.tableName;
    }
    public void setTableName(String tableName){
        this.tableName = tableName;
    }
    public String getPrimaryKeyName(){
        return this.primaryKeyName;
    }
    public void setPrimaryKeyName(String primaryKeyName){
        this.primaryKeyName = primaryKeyName;
    }

    public Object getParent() {
        return parent;
    }

    public void setParent(Object parent) {
        this.parent = parent;
    }
    
    public Field[] getForeignSameFields() {
        return foreignSameFields;
    }

    public void setForeignSameFields(Field[] foreignSameFields) {
        this.foreignSameFields = foreignSameFields;
    }
    
    //CONSTRUCTOR
    public BddObject(){
        init();
    }

    //METHODS
    public void init(){
            this.setTableName(DaoUtility.getTableName(this));
            this.setPrimaryKeyName(DaoUtility.getPrimaryKeyColumnName(this));
    }

    /**
     * 
     * @param con
     * @param name
     * @return
     * @throws Exception 
     */
    public String getValues(Connection con,String name) throws Exception{
        String values = "(";
        List<Field> lst = DaoUtility.getAllColumnFields(this);
        for(Field field : lst){
            Method method = DaoUtility.getGetter(this, field);
            Class<?> returnParam = method.getReturnType();
            if(method.equals(DaoUtility.getPrimaryKeyGetMethod(this)) && method.invoke(this, (Object[]) null) == null && returnParam.equals(String.class))
                values += DaoUtility.convertForSql(constructPK(con, name)) + ", ";  
            else if(method.equals(DaoUtility.getPrimaryKeyGetMethod(this)) && method.invoke(this, (Object[]) null) == null && returnParam.equals(Integer.class))
                values += constructPK(con, name) + ", ";
            else if(field.isAnnotationPresent(ForeignKey.class)){
                if(field.getAnnotation(ForeignKey.class).foreignType() == ForeignType.OneToMany || field.getAnnotation(ForeignKey.class).foreignType() == ForeignType.OneToOne){
                    BddObject temp = (BddObject) method.invoke(this, (Object[]) null);
                    values += "'" + DaoUtility.getPrimaryKeyGetMethod(temp).invoke(temp, (Object[]) null) + "', ";
                }
            }
            else if(method.invoke(this, (Object[]) null) == null){
                values += "default";
            }else{ 
                values += DaoUtility.convertForSql(method.invoke(this, (Object[]) null)) + ", "; 
            }
//            System.out.println(values);
        }
        values = values.substring(0, values.lastIndexOf(','));
        values = values + ")";
        return values;
    }
    /**
     * 
     * @param con
     * @throws Exception 
     */
    public void save(Connection con) throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            String query = "INSERT INTO " + this.getTableName() + DaoUtility.getListColumns(this)+" VALUES " + this.getValues(con, "DefaultConnection");
            // System.out.println(query);
            PreparedStatement stmt =  con.prepareStatement(query);
            stmt.executeUpdate();
        }finally {
            if(state == true) con.close();
        }
    }
    
    public void save(Connection con,String name) throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect(name);
                state = true;
            }
            String query = "INSERT INTO " + this.getTableName() + DaoUtility.getListColumns(this)+" VALUES " + this.getValues(con, name);
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
    public void deleteAll(Connection con) throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            String query = "DELETE FROM " + this.getTableName();
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.executeUpdate();
        }finally {
            if(state == true) con.close();
        }
    }
    
    /**
     * 
     * @param con
     * @param condition
     * @throws Exception 
     */
    public void deleteWhere(Connection con, String condition) throws Exception {
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            String query = "DELETE FROM " + this.getTableName() + " WHERE " + condition;
            // System.out.println(query);
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.executeUpdate();
        }finally {
            if(state == true) con.close();
        }    
    }

    public void delete(Connection con) throws Exception {
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            String condition = this.getPrimaryKeyName()  + " = '" + DaoUtility.getPrimaryKeyGetMethod(this).invoke(this, (Object[]) null) + "'";
            this.deleteWhere(con, condition);
        }finally {
            if(state == true) con.close();
        }
    }
    /**
     * 
     * @param con
     * @param id
     * @throws Exception 
     */
    public void deleteById(Connection con, Object id) throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            String condition = DaoUtility.getPrimaryKeyName(this)  +" = '" + id +"'";
            this.deleteWhere(con, condition);
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
    public String updatedValues() throws Exception{
        String query = "";
        List<Field> lst = DaoUtility.getAllColumnFields(this);
        for(Field field : lst){
            Method method = DaoUtility.getGetter(this, field);
            if(field.isAnnotationPresent(ForeignKey.class)){
                ForeignKey fk = field.getAnnotation(ForeignKey.class);
                if(fk.foreignType() == ForeignType.OneToMany || fk.foreignType() == ForeignType.OneToOne){
//                    BddObject temp = (BddObject) method.invoke(this, (Object[]) null);
//                    values += "'" + DaoUtility.getPrimaryKeyGetMethod(temp).invoke(temp, (Object[]) null) + "', ";
                    BddObject temp = (BddObject) method.invoke(this, (Object[]) null);
                    query += fk.mappedBy() + " = '" + DaoUtility.getPrimaryKeyGetMethod(temp).invoke(temp, (Object[]) null) + "', ";
                
                }
            }else
                query += DaoUtility.getName(field) + " = '" + method.invoke(this, (Object[]) null) + "', ";
        }
        query = query.substring(0, query.lastIndexOf(','));
        return query;
    }
    /**
     * 
     * @param con
     * @throws Exception 
     */
    public void update(Connection con) throws Exception {
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            String query = "UPDATE "+ this.getTableName() +" SET " + this.updatedValues();
            query += " WHERE " + this.getPrimaryKeyName() +" = '" + DaoUtility.getPrimaryKeyGetMethod(this).invoke( this, (Object[]) null)+"'";
            // System.out.println(query);
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.executeUpdate();
        }finally{
            if(state == true) con.close();
        }    
    }
    //SELECT
    /**
     * 
     * @param <T>
     * @param con
     * @return
     * @throws Exception 
     */
    public <T> List<T> findAll(Connection con)throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            String query = "SELECT * FROM " + this.getTableName();
            // System.out.println(query);
            List<T> list = this.fetch(con, query);
            return list;
        }finally {
            if(state == true) con.close();
        }
    }
    
    /**
     * 
     * @param <T>
     * @param con
     * @param tableName
     * @return
     * @throws Exception 
     */
    public <T> List<T> findAllFromTable(Connection con, String tableName)throws Exception{
        boolean state = false;
        if(con == null){
            con = new DbConnection().connect();
            state = 
            true;
        }
        String query = "SELECT * FROM " + tableName;
        List<T> list = this.fetch(con, query);
        if(state == true) con.close();
        return list;
    }
    
    /**
     * 
     * @param <T>
     * @param con
     * @param id
     * @return
     * @throws Exception 
     */
    public <T> T findById(Connection con, Object id)throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            String condition = DaoUtility.getPrimaryKeyName(this) + " = '" + id + "'";
            T obj = (T) this.findWhere(con, condition).get(0);
            return (T) obj;
        }catch( Exception e ){
            e.printStackTrace();
            throw e;
        }
        finally {
            if(state == true) con.close();
        }
    }
    
    /**
     * 
     * @param <T>
     * @param con
     * @return
     * @throws Exception 
     */
    public <T> T findById(Connection con)throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            String condition = this.getPrimaryKeyName() + " = '" + DaoUtility.getPrimaryKeyGetMethod(this).invoke(this, (Object[])null) + "'";
            T obj = (T) this.findWhere(con, condition).get(0);
            return obj;
        }
        finally {
            if(state == true) con.close();
        }
    }
    
    /**
     * 
     * @param <T>
     * @param con
     * @return
     * @throws Exception 
     */
    public <T>  List<T> findWhere(Connection con) throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            String condition = DaoUtility.getConditionByAttributeValue(this);
            String query = "SELECT * FROM " + this.getTableName() + condition;
            // System.out.println(query);
            List<T> lst = this.fetch(con, query);
            return lst;
        }finally{
            if( state == true) con.close();
        }
    }
    
    /**
     * 
     * @param <T>
     * @param con
     * @param condition
     * @return
     * @throws Exception 
     */
    public <T>  List<T> findWhere(Connection con, String condition) throws Exception {
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            String query = "SELECT * FROM " + this.getTableName() + " WHERE " + condition;
            System.out.println(query);
            List<T> lst = this.fetch(con, query);
            return lst;
        }finally {
                if(state == true) con.close();
        }
    }
    
    //OTHERS
    
    /**
     * 
     * @param con
     * @param query
     * @throws Exception 
     */
    public void executeUpdate(Connection con, String query) throws Exception{
        boolean state = false;   
        try{     
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            PreparedStatement stmt =  con.prepareStatement(query);
            stmt.executeUpdate();
        }finally {
                if(state == true) con.close();
        }
    }
    
    /**
     * 
     * @param <T>
     * @param con
     * @param query
     * @param obj
     * @return
     * @throws Exception 
     */
    public <T>  List<T> executeQuery(Connection con, String query, Object obj) throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            List<T> list = new ArrayList<>();
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            List<Field> fields = DaoUtility.getAllColumnFields(this);
            List<Method> methods = DaoUtility.getAllSettersMethod(this);
            List<String> columns = DaoUtility.getTableColumns(con, this.getTableName());
            while( rs.next() ){
                T now = this.convertToObject(con, rs, fields, methods, obj, columns);
                list.add(now);
            }
            return list;
        }finally {
            if(state == true) con.close();
        }
    }
    
    /**
     * 
     * @param <T>
     * @param con
     * @param query
     * @return
     * @throws Exception 
     */
    public <T> List<T> fetch( Connection con, String query ) throws Exception{
        List<T> list = new ArrayList<>();
        PreparedStatement stmt = con.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        List<Field> fields = ( this.getForeignSameFields() != null ) 
                ? DaoUtility.getAllColumnFields(this, this.getForeignSameFields()) 
                : DaoUtility.getAllColumnFields(this);
        List<Method> methods = DaoUtility.getAllSettersMethod(this);
        List<String> columns = DaoUtility.getTableColumns(con, this.getTableName());
        while( rs.next() ){
            T now = this.convertToObject(con, rs, fields, methods, columns);
            list.add(now);
        }
        return list;
    }
    
    /**
     * 
     * @param <T>
     * @param con
     * @param resultSet
     * @param fields
     * @param methods
     * @param obj
     * @param columns
     * @return
     * @throws Exception 
     */
    private <T> T convertToObject(Connection con, ResultSet resultSet, List<Field> fields, List<Method> methods, Object obj, List<String> columns) throws Exception{
        Object object = obj.getClass().getDeclaredConstructor().newInstance();
        for (String column : columns) {
            for( int i = 0; i < fields.size() ; i++ ){
                if(DaoUtility.getName(fields.get(i)).equals(column) ){
                    Method method = methods.get(i);
                    Object value = resultSet.getObject(column);
                    if(fields.get(i).isAnnotationPresent(ForeignKey.class)){
                        value = treatForeignKey(con, value, fields.get(i), (BddObject) object);
                    }if(value == null)
                        value = ObjectUtility.getPrimitiveDefaultValue(fields.get(i).getType());                    
                    method.invoke(object, value);
                }
            }
        }
        return (T) object;
    }
    
    /**
     * 
     * @param <T>
     * @param con
     * @param resultSet
     * @param fields
     * @param methods
     * @param columns
     * @return
     * @throws Exception 
     */

    private <T>  T convertToObject(Connection con, ResultSet resultSet, List<Field> fields, List<Method> methods, List<String> columns) throws Exception{
        Object object = this.getClass().getDeclaredConstructor().newInstance();
        for (String column : columns) {
            for( int i = 0; i < fields.size() ; i++ ){
                if(DaoUtility.getName(fields.get(i)).equals(column)  && fields.get(i).canAccess(object)){
                    Method method = methods.get(i);
                    Object value = resultSet.getObject(column);
                    // System.out.println("methods : "+method+" value : "+value);
                    if(fields.get(i).isAnnotationPresent(ForeignKey.class)){
                        value = treatForeignKey(con, value, fields.get(i), (BddObject) object);
                    }if(value == null)
                        value = ObjectUtility.getPrimitiveDefaultValue(fields.get(i).getType()); 
                    method.invoke(object, value);
                }
            }    
        }
        return (T) object;
    }  
    
    /**
     * 
     * @param con
     * @param name
     * @return
     * @throws Exception 
     */
    public String constructPK(Connection con, String name)throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect(name);
                state = true;
            }
            String[] detail = DaoUtility.getPrimaryKeyDetails(this);
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
    public Object treatForeignKey(Connection con, Object value, Field field, BddObject object) throws Exception{
        BddObject temp = null;
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
        temp = (BddObject) Class.forName(classForName).getDeclaredConstructor().newInstance();
        Field[] tests = this.getSameFields( temp );
        for (Field test : tests) {
            if(test.getType() == this.getClass()){
                temp.setParent(this);
            }
        }
        temp.setForeignSameFields(tests);
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
    public static Object createForeignKeyObject(Connection con, Field field, BddObject foreignKey, Object value, BddObject object) throws Exception{
        Object obj = null;
        if(field.getAnnotation(ForeignKey.class).foreignType() == ForeignType.OneToMany || field.getAnnotation(ForeignKey.class).foreignType() == ForeignType.OneToOne){
            if(foreignKey.getParent() != null){
                Method setter = DaoUtility.getSetter(obj, field);
                setter.invoke(obj, foreignKey.getParent());
            }else{
                obj = foreignKey.findById(con, value);
            }
        }
        else if(field.getAnnotation(ForeignKey.class).foreignType() == ForeignType.ManyToOne || field.getAnnotation(ForeignKey.class).foreignType() == ForeignType.ManyToMany){
            String condition = DaoUtility.getName(field) + " = '" + DaoUtility.getPrimaryKeyGetMethod(object).invoke(object, (Object[])null) + "'";
            obj = foreignKey.findWhere(con, condition);
            ArrayList<Object> objects = (ArrayList) obj;
            for( Object o : objects ){
                for( Field f : foreignKey.getForeignSameFields() ){
                    Method setter = DaoUtility.getSetter(o, f);
                    setter.invoke(o, foreignKey.getParent());
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
    public boolean sameClassExistence( Field field ) throws Exception{
        return field.isAnnotationPresent(ForeignKey.class) && field.getType() == this.getClass();
    }
    /**
     * 
     * @param object
     * @return
     * @throws Exception 
     */
    public Field[] getSameFields( Object object ) throws Exception{
        Field[] fields = object.getClass().getDeclaredFields();
        List<Field> fs = new ArrayList<>();
        for( Field field : fields ){
            if( this.sameClassExistence(field) ){
                fs.add( field );
            }
        }
        return fs.toArray( new Field[0] );
    }
}
