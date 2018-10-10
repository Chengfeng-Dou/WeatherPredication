package util;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by douchengfeng on 2018/10/10.
 *
 */

public class XmlParser {

    public <T> T parseSimpleObjectFromXML(Class<T> tClass, String xml){
        Map<String, Field> fieldsNameMap = getFieldsNameMap(tClass);
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(new StringReader(xml));
            T obj = tClass.newInstance();

            int type = parser.getEventType();
            while(type != XmlPullParser.END_DOCUMENT){
                parseType(parser, type, fieldsNameMap, obj);
                type = parser.next();
            }

            return obj;

        } catch (XmlPullParserException | IllegalAccessException | InstantiationException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private <T> void setFieldValue(XmlPullParser parser, Field field, T obj) throws IOException, XmlPullParserException, IllegalAccessException, InstantiationException {
        if(field.getType() == String.class){    //如果是属性，则直接填写
            setFiledValue1(field, obj, parser.nextText(), String.class);
            return;
        }

        if(field.getType().isArray()){
            String rootTag = parser.getName();
            int type = parser.next();

            List<Object> list = new ArrayList<>();
            while(!(type == XmlPullParser.END_TAG && parser.getName().equals(rootTag))){
                list.add(constructSingleObject(parser, field.getType().getComponentType()));
                type = parser.next();
            }

            Object[] array = list.toArray();
            setFiledValue1(field, obj, array, array.getClass());

            return;
        }

        setFiledValue1(field, obj, constructSingleObject(parser, field.getType()), field.getType());
    }

    private <T> void setFiledValue1(Field field, T obj, Object value, Class argType){
        char[] nameArray= field.getName().toCharArray();
        nameArray[0] -= 32;
        String methodName = "set" + String.valueOf(nameArray);

        try {
            Method method = obj.getClass().getDeclaredMethod(methodName, argType);
            method.invoke(obj, value);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private <T> T constructSingleObject(XmlPullParser parser, Class<T> tClass) throws XmlPullParserException, IOException, IllegalAccessException, InstantiationException {
        int type = parser.getEventType();
        String rootTag = parser.getName();
        //Log.d("myWeather", rootTag);

        Map<String, Field> fieldsNameMap = getFieldsNameMap(tClass);
        T obj = tClass.newInstance();

        while(!(type == XmlPullParser.END_TAG && parser.getName().equals(rootTag))){
            parseType(parser, type, fieldsNameMap, obj);
            type = parser.next();
        }

        return obj;
    }

    private <T> void parseType(XmlPullParser parser, int type, Map<String, Field> fieldsNameMap, T obj) throws IOException, XmlPullParserException, IllegalAccessException, InstantiationException {
        switch (type){
            case XmlPullParser.START_TAG:
                String tagName = parser.getName();
                if(fieldsNameMap.containsKey(tagName)){
                    setFieldValue(parser, fieldsNameMap.get(tagName), obj);
                }
        }
    }


    private <T> Map<String, Field> getFieldsNameMap(Class<T> tClass){
        Field[] fields = tClass.getDeclaredFields();
        Map<String, Field> fieldsNameMap = new HashMap<>();
        for(Field field: fields){
            fieldsNameMap.put(field.getName(), field);
        }
        return fieldsNameMap;
    }

}
