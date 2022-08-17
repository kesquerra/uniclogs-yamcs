package org.oresat.mdb;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public class MdbParser {
    JsonReader reader;

    public MdbParser(String file) throws IOException {
        this.reader = new JsonReader(new FileReader(file));
    }

    private MdbDataType parseDataType() throws IOException {
        MdbDataType dtype = new MdbDataType();
        this.reader.beginObject();
        while (this.reader.hasNext()) {
            JsonToken nextToken = this.reader.peek();
            System.err.println(nextToken);
            if (nextToken.equals(JsonToken.NAME)) {
                String name = this.reader.nextName();
                System.err.println(name);
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

        //TODO: add custom exceptions for no name, type, etc
        return dtype;
    }

    private List<MdbDataType> parseDataTypeArray() throws IOException {
        ArrayList<MdbDataType> dtypes = new ArrayList<>();

        this.reader.beginArray();
        while (this.reader.hasNext()) {
            if (this.reader.peek().equals(JsonToken.BEGIN_OBJECT)) {
                dtypes.add(this.parseDataType());
            }
        }
        this.reader.endArray();
        return dtypes;
    }

    public List<MdbDataType> parse() throws IOException {
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
                    if (name.equals("datatypes")) {
                        dtypes.addAll(this.parseDataTypeArray());
                    }
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
        return dtypes;
    }
    
}
