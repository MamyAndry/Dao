/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dao.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 *
 * @author Mamisoa
 */
public class JsonUtility {
    
    @SuppressWarnings("unchecked")
    public static <T> T parseJson(String path, Class<?> objectClass) throws Exception{
        JsonReader reader = new JsonReader(new BufferedReader(new FileReader(path)));
        Object temp = new Gson().fromJson(reader, objectClass);
        return (T)temp;
    }
    public static String encodeJson(Object object) throws Exception{
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        return builder.create().toJson(object);
    }
}
