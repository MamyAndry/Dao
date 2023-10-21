package dao;

import java.sql.*;

import dao.xml.XmlConnection;

public class DbConnection{
    String port;
    String host;
    String base;
    String username;
    String password;
    String database;

    //SETTERS
    public void setHost(String h){this.host=h;}
    public void setPort(String p){this.port=p;}
    public void setUsername(String u){this.username=u;}
    public void setPassword(String p){this.password=p;}
    public void setBase(String b){this.base=b;}
    
    public void setDatabase(String d){this.database=d;}

    //GETTERS    
    public String getHost(){return this.host;}
    public String getPort(){return this.port;}
    public String getUsername(){return this.username;}
    public String getPassword(){return this.password;}
    public String getDatabase(){return this.database;}
    public String getBase(){return this.base;}

    //CONSTRUCTOR
    public DbConnection() throws Exception{
        XmlConnection con = XmlConnection.createConnection();
//        System.out.println(con.get );
        this.setHost(con.getHost());
        this.setPort(con.getPort());
        this.setBase(con.getBase());
        this.setUsername(con.getUser());
        this.setPassword(con.getPassword());
        this.setDatabase(con.getDatabase());
    }
    //METHODS

    public Connection connectToOracle() throws Exception{
        Class.forName("oracle.jdbc.driver.OracleDriver");
        // Connection con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl",this.getUsername(),this.getPassword());
        Connection con=DriverManager.getConnection("jdbc:oracle:thin:@"+this.getHost()+":"+this.getPort()+":orcl",this.getUsername(),this.getPassword());
        return con;
    }

    public Connection connectToMysql() throws Exception{
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con=DriverManager.getConnection("jdbc:mysql://"+this.getHost()+":"+this.getPort()+"/"+this.getDatabase(),this.getUsername(),this.getPassword());
        return con;
    }

    public Connection connectToPostgres() throws Exception{
        Class.forName("org.postgresql.Driver");
        Connection con = DriverManager.getConnection("jdbc:postgresql://"+this.getHost()+":"+this.getPort()+"/"+this.getDatabase(),this.getUsername(),this.getPassword());
        return con;
    }

    public Connection connection() throws Exception{
        System.out.println(this);
        if(this.getBase().equals("PostgreSQL"))
            return this.connectToPostgres();
        else if(this.getBase().equals("NySQL"))
            return this.connectToMysql();
        else if(this.getBase().equals("Oracle"))
            return this.connectToOracle();
        return null; 
    }
}
