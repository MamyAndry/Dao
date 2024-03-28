/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dao.database;

import com.dao.utils.Misc;
import com.dao.file.JsonUtility;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;

/**
 *
 * @author Mamisoa
 */
public class DbConnection {
    final static String confPath = "database.json";
    private String defaultConnection = "DefaultConnection";
    private String inUseConnection;    
    private DbProperties inUseDbProperties;
    private boolean init = false;
    private Connection connection = null;
    private HashMap<String, DbProperties> listConnection;

    public void setListConnection(HashMap<String, DbProperties> listConnection) {
        this.listConnection = listConnection;
    }
    public  String getConfPath() {
        return confPath;
    }

    public String getDefaultConnection() {
        return defaultConnection;
    }

    public void setDefaultConnection(String defaultConnection) {
        this.defaultConnection = defaultConnection;
    }

    public DbProperties getInUseDbProperties() {
        return inUseDbProperties;
    }

    public void setInUseDbProperties(DbProperties inUseDbProperties) {
        this.inUseDbProperties = inUseDbProperties;
    }

    public String getInUseConnection() {
        return inUseConnection;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public HashMap<String, DbProperties> getListConnection() {
        return listConnection;
    }

    //METHODS
    public void read()throws Exception{
        String separator = File.separator;
        String confFile = Misc.getConnectionConfLocation() + separator + getConfPath();
        DbConnection temp = JsonUtility.parseJson(confFile, this.getClass());
        this.setListConnection(temp.getListConnection());
        this.setDefaultConnection(temp.getDefaultConnection());
        this.setInUseConnection(temp.getDefaultConnection());
    }

    public void setInUseConnection(String inUseConnection){
        if(getListConnection().get(inUseConnection) != null){
            this.inUseConnection = inUseConnection;
            this.setInUseDbProperties(this.getListConnection().get(inUseConnection));
        }
        else throw new IllegalArgumentException("There is no such connection : " + inUseConnection);
    }

    public Connection createConnection(String connection)throws Exception{
        if(!isInit()) init();
        DbProperties prop = this.getListConnection().get(connection);
        this.setInUseDbProperties(prop);
        return prop.connect();
    }

    /**
     * connect to the database by changing
     * the connection property
     * @author rakharrs
     * @return
     * @throws Exception
     */
    public Connection connect()throws Exception{
        if(!isInit()) {
            init();
        }
        setConnection(createConnection(getInUseConnection()));
        return getConnection();
    }

    public void init() throws Exception{
        setInit(true);
        read();
    }

    public Connection connect(String connection)throws Exception{
        if(!isInit()) init();
        setConnection(createConnection(connection));
        return getConnection();
    }

    /**
     * Check if the connection property is null or not
     * @return
     * @throws Exception
     */
    public boolean isConnected() throws Exception {
        return getConnection() != null;
    }

    /**
     * get the connection property
     * if it is undefined -> create the property
     * by connecting to the database
     * @author rakharrs
     * @return - connection property
     * @throws Exception
     */
    public Connection getConnection() throws Exception{
        if(this.connection == null) this.connect();
        return this.connection;
    }

    /**
     * @author rakharrs
     */
    public void close() throws Exception {
        getConnection().close();
    }

    /**
     * @author rakharrs
     */
    public void commit() throws Exception {
        getConnection().commit();
    }
}
