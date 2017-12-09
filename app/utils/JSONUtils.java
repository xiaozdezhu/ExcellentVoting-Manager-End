package utils;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.node.MissingNode;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Felix on 5/28/15.
 */
public final class JSONUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JSONUtils.class);
    public static final ObjectMapper OBJECT_MAPPER;
    private static boolean pretty=false;

    public static final TypeReference<Map<String, Object>> MAP_TYPE_REF = new TypeReference<Map<String, Object>>() {
    };
    public static final TypeReference<List<Map<String, Object>>> LIST_MAP_TYPE_REF = new TypeReference<List<Map<String, Object>>>() {
    };


    static{
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        OBJECT_MAPPER.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static JsonNode toJsonNode(String json){
        JsonNode jsonNode = MissingNode.getInstance();
        try {
            jsonNode = OBJECT_MAPPER.readValue(json, JsonNode.class);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return jsonNode;
    }

    public static JsonNode toJsonNode(Object obj) {
        String json = toJson(obj);
        return toJsonNode(json);
    }

    public static <T> T toClzInstance(String json, Class<T> clz) {
        T obj = null;
        try {
            obj = OBJECT_MAPPER.readValue(json, clz);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return obj;
    }

    public static <T> T toClzInstance(Object object, Class<T> clz) {
        return toClzInstance(toString(object), clz);
    }

    public static String toString(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }



    public static String toJson(Object obj){
        return toJson(obj, false);
    }

    public static String toJson(Object obj,boolean pretty){
        String json=null;
        try {
            if(pretty){
                json = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            }else{
                json = OBJECT_MAPPER.writeValueAsString(obj);
            }
        } catch (IOException e) {
        }

        return json;
    }

    public static <T> T toObject(String json, Class<T> classz){
        JsonNode jsonNode = MissingNode.getInstance();

        try {
            jsonNode = OBJECT_MAPPER.readValue(json, JsonNode.class);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return toObject(jsonNode,classz);
    }

    public static <T> T toObject(JsonNode jsonNode, Class<T> classz) {
        try {
            return (T)OBJECT_MAPPER.readValue(jsonNode,classz);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public static <T> T toObject(String json, TypeReference<T> valueTypeRef) {
        if(null != json){
            try {
                return (T)OBJECT_MAPPER.readValue(json, valueTypeRef);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
        return null;
    }

    public static <T> T toObject(JsonNode node, TypeReference<T> valueTypeRef){
        if(null !=node){
            try {
                return (T)OBJECT_MAPPER.readValue(node, valueTypeRef);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
        return null;
    }

    public static Map<String, Object>  toMap(String json) {
        if (!StringUtils.isBlank(json)) {
            try {
                return OBJECT_MAPPER.convertValue(json, MAP_TYPE_REF);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
        return null;
    }

    public static Map<String, Object> toMap(Object obj) {
        if (null != obj) {
            try {
                return OBJECT_MAPPER.convertValue(obj, MAP_TYPE_REF);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
        return null;
    }





}
