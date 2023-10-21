package dao.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public class XmlConnection {
    
    String host;
    String port;
    String database;
    String user;
    String password;
    String base;

    public void setHost(String host) throws Exception {
        if (host == null) throw new Exception("Host est null");
        if (host.isEmpty()) throw new Exception("Host est vide");
        this.host = host;
    }
    
    public String getHost() {
        return host;
    }
    
    public void setBase(String base) throws Exception {
        if (base == null) throw new Exception("base est null");
        if (base.isEmpty()) throw new Exception("Host est vide");
        this.base = base;
    }
    public String getBase() {
        return base;
    }

    public void setPort(String port) throws Exception {
        if (port == null) throw new Exception("Port est null");
        if (port.isEmpty()) throw new Exception("Port est vide");
        this.port = port;
    }

    public String getPort() {
        return port;
    }

    public void setDatabase(String database) throws Exception {
        if (database == null) throw new Exception("Database name est null");
        if (database.isEmpty()) throw new Exception("Database name est vide");
        this.database = database;
    }

    public String getDatabase() {
        return database;
    }

    public void setPassword(String password) throws Exception {
        if (password == null) throw new Exception("Database name est null");
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setUser(String user) throws Exception {
        if (user == null) throw new Exception("Database name est null");
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public XmlConnection(String host, String port, String database, String user, String password, String base) throws Exception {
        this.setHost(host);
        this.setPort(port);
        this.setDatabase(database);
        this.setUser(user);
        this.setPassword(password);
        this.setBase(base);
    }

    public static XmlConnection createConnection() throws Exception {
        File inputFile = new File("config.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("connection");
        
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
//            System.out.println("\nCurrent Element :" + nNode.getNodeName());
            
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) nNode;
                String host = element.getElementsByTagName("host").item(0).getTextContent();
                String port = element.getElementsByTagName("port").item(0).getTextContent();
                String database = element.getElementsByTagName("database").item(0).getTextContent();
                String user = element.getElementsByTagName("user").item(0).getTextContent();
                String password = element.getElementsByTagName("password").item(0).getTextContent();
                String type = element.getAttribute("type");
//                System.out.println(type);
                return new XmlConnection(host, port, database, user, password, type);
            }
        }
        
        throw new Exception("Configuration de Base de donnee introuvable");
    }

}
