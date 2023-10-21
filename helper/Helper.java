package dao.helper;

import java.lang.reflect.*;
import dao.annotation.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Helper{

//METHODS

    public static String capitalize(String text){
        return text.substring(0,1).toUpperCase().concat(text.substring(1));
    }

    public static String getTableName(Object obj) { //Return the name of the Table
        if(obj.getClass().isAnnotationPresent(Table.class)){
            Table annotation = obj.getClass().getAnnotation(Table.class);
            if(!annotation.name().equals(""))return annotation.name();
        }
        String str = obj.getClass().getName();
        return str.split("\\.")[str.split("\\.").length - 1];
    }

    public static Method[] getGetters(Object obj) throws Exception{
        List<Field> list = getColumnFields(obj);
        Method[] res = new Method[list.size()];
        int i = 0;
        for(Field field : list){
            res[i] = obj.getClass().getDeclaredMethod("get" + capitalize(field.getName()));
            i++;
        }
        return res;
    }

    public static String[] convertIntoArray(ArrayList<String> list){
        String[] res = new String[list.size()];
        for(int i = 0 ; i < list.size() ; i++){
            res[i] = list.get(i);
        }
        return res;
    }

    public static List<Field> getColumnFields(Object obj){
        List<Field> res = new ArrayList<Field>();
        for(int i = 0 ; i < obj.getClass().getDeclaredFields().length ; i++){
            if(obj.getClass().getDeclaredFields()[i].isAnnotationPresent(Column.class))
                res.add(obj.getClass().getDeclaredFields()[i]);
        }
        return res;
    }

    public static String[] getColumns(Object obj){
        List<Field> lst = getColumnFields(obj);
        String[] res = new String[lst.size()];
        int i = 0;
        for(Field field : lst){
            Column col = field.getAnnotation(Column.class);
            if(col.name().equals(""))
                res[i] = capitalize(field.getName());
            else
                res[i] = capitalize(col.name());
            i++;
        }
        return res;
    }

    public static String[] getTypeName(Object obj){
        List<Field> lst = getColumnFields(obj);
        String[] res = new String[lst.size()];
        int i = 0;
        for (Field field : lst) {
            res[i] = field.getType().getName();
            i++;
        }
        return res;
    }

    public static String getPKName(Object obj){
        String res = "";
        for(int i = 0 ; i < obj.getClass().getDeclaredFields().length ; i++){
            if(obj.getClass().getDeclaredFields()[i].isAnnotationPresent(PrimaryKey.class) && obj.getClass().getDeclaredFields()[i].isAnnotationPresent(Column.class)){
                Column col = obj.getClass().getDeclaredFields()[i].getAnnotation(Column.class);
                if(col.name().equals(""))
                    res = capitalize(obj.getClass().getDeclaredFields()[i].getName());
                else if(col.name().equals("") == false)
                    res = capitalize(col.name());
            }
        }
        return res;
    }
    
    public static Method getPK(Object obj) throws Exception{
        String res = "";
        for(int i = 0 ; i < obj.getClass().getDeclaredFields().length ; i++){
            if(obj.getClass().getDeclaredFields()[i].isAnnotationPresent(PrimaryKey.class) && obj.getClass().getDeclaredFields()[i].isAnnotationPresent(Column.class)){
                res = capitalize(obj.getClass().getDeclaredFields()[i].getName());
            }
        }
        return obj.getClass().getMethod("get" + res);
    }

    public boolean checkIfRedundant(String mot,Enumeration<String> lst){
        while(lst.hasMoreElements()){
            String str = lst.nextElement();
            if(str.equals(mot)) return true;
        }
        return false;
    }
}
