package org.oresat.mdb;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import org.yamcs.xtce.Parameter;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;



public class MdbParser{
    JsonReader reader;
    Mdb mdb;

    public MdbParser(String file) throws IOException {
        this.reader = new JsonReader(new FileReader(file));
        this.mdb = new Mdb();
    }

    private MdbDataType parseDataType() throws IOException {
        MdbDataType dtype = new MdbDataType();
        this.reader.beginObject();
        while (this.reader.hasNext()) {
            JsonToken nextToken = this.reader.peek();
            if (nextToken.equals(JsonToken.NAME)) {
                String name = this.reader.nextName();
                if (name.equals("name")) {
                    dtype.setName(this.reader.nextString());
                } else if (name.equals("type")) {
                    dtype.setType(this.reader.nextString());
                } else if (name.equals("byteOrder")) {
                    dtype.setByteOrder(this.reader.nextString());
                } else if (name.equals("size")) {
                    dtype.setSizeInBits(this.reader.nextInt());
                } else if (name.equals("encoding")) {
                    dtype.setEncoding(this.reader.nextString());
                }
            }
        }
        this.reader.endObject();

        //TODO: add custom exceptions for missing required fields, additional unknown fields
        return dtype;
    }

    private MdbParameter parseParameter() throws IOException {
        MdbParameter param = new MdbParameter();
        this.reader.beginObject();
        while (this.reader.hasNext()) {
            JsonToken nextToken = this.reader.peek();
            if (nextToken.equals(JsonToken.NAME)) {
                String name = this.reader.nextName();
                if (name.equals("name")) {
                    param.setName(this.reader.nextString());
                } else if (name.equals("description")) {
                    param.setDescription(this.reader.nextString());
                } else if (name.equals("datatype")) {
                    param.setDataType(this.mdb.getDataType(this.reader.nextString()));
                }
            }
        }
        this.reader.endObject();
        return param;
    }

    private<T extends Parseable> List<T> parseTypeArray(Class<T> tClass) throws IOException, ReflectiveOperationException {
        List<T> types = new ArrayList<>();

        this.reader.beginArray();
        while (this.reader.hasNext()) {
            if (this.reader.peek().equals(JsonToken.BEGIN_OBJECT)) {
                types.add(this.parseType(tClass));
            }
        }
        this.reader.endArray();

        return types;
    }

    private<T extends Parseable> T parseType(Class<T> tClass) throws IOException, ReflectiveOperationException {
        T t = tClass.getConstructor().newInstance();
        this.reader.beginObject();
        Field[] fields = tClass.getDeclaredFields();
        HashMap<String, Field> fieldMap = new HashMap<>();
        for (Field f: fields) {
            fieldMap.put(f.getName(), f);
        }
        while (this.reader.hasNext()) {
            JsonToken nextToken = this.reader.peek();
            System.err.println("Token: " + nextToken);
            if (nextToken.equals(JsonToken.NAME)) {
                String name = this.reader.nextName();
                if (fieldMap.containsKey(name)) {
                    System.err.println("Found Name: " + name);
                    if (fieldMap.get(name).getType().equals(MdbDataType.class)) {
                        t.getClass().getMethod("setDataType", MdbDataType.class).invoke(t, this.mdb.getDataType(this.reader.nextString()));
                    } else {
                        t.setField(name, this.reader.nextString());
                    }
                } else {
                    System.err.println("Unknown Name: " + name);
                    this.reader.nextString();
                }
            }
        }
        this.reader.endObject();
        return t;
    }

    private List<MdbDataType> parseDataTypeArray() throws IOException, ReflectiveOperationException {
        ArrayList<MdbDataType> dtypes = new ArrayList<>();

        this.reader.beginArray();
        while (this.reader.hasNext()) {
            if (this.reader.peek().equals(JsonToken.BEGIN_OBJECT)) {
                dtypes.add(this.parseType(MdbDataType.class));
            }
        }
        this.reader.endArray();
        return dtypes;
    }

    public void parseObjects(String name) throws IOException, ReflectiveOperationException {
        switch (name) {
            case "datatypes":
                this.mdb.setDataTypes(this.parseTypeArray(MdbDataType.class));
                break;
            case "parameters":
                this.mdb.setParameters(this.parseTypeArray(MdbParameter.class));
                break;
            case "arguments":
                this.mdb.setCommands(this.parseTypeArray(MdbCommand.class));
                break;
            case "commands":
                this.mdb.setCommands(this.parseTypeArray(MdbCommand.class));
                break;
            default:
                return;
            
        }
    }

    

    public Mdb parse() throws IOException, ReflectiveOperationException {
        ArrayList<MdbDataType> dtypes = new ArrayList<>();
        while (this.reader.hasNext()) {
            JsonToken nextToken = this.reader.peek();

            switch (nextToken) {
                case BEGIN_OBJECT:
                    this.reader.beginObject();
                    System.err.print("begin obj");
                    break;
                case NAME:
                    String name = this.reader.nextName();
                    this.parseObjects(name);
                    break;
                case STRING:
                    String str = this.reader.nextString();
                    System.err.println("String: " + str);
                    break;
                case NUMBER:
                    Integer value = this.reader.nextInt();
                    System.err.println("Value: " + value.toString());
                    break;
                case NULL:
                    this.reader.nextNull();
                    break;
                case END_OBJECT:
                    this.reader.endObject();
                    System.err.print("end obj");
                    break;
                case BOOLEAN:
                    Boolean bool = this.reader.nextBoolean();
                    System.err.println("Bool: " + bool.toString());
                    break;
                case BEGIN_ARRAY:
                    System.err.print("begin array");
                    this.reader.beginArray();
                    break;
                case END_ARRAY:
                    this.reader.endArray();
                    System.err.print("end array");
                    break;
                case END_DOCUMENT:
                    this.reader.close();
                    break;
            }
        }
        this.mdb.setDataTypes(dtypes);
        return this.mdb;
    }
    
}
