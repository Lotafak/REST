package pl.patryk;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by Pyra on 2016-04-20.
 */
public class MapperUtil {

    static com.fasterxml.jackson.databind.ObjectMapper MAPPER = new ObjectMapper();

    public static <T> T readAsObjectOf(Class<T> clazz, String value){
        try {
            return MAPPER.readValue(value, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
