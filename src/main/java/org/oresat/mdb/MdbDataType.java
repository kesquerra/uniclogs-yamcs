package org.oresat.mdb;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteOrder;

import org.yamcs.xtce.Argument;
import org.yamcs.xtce.ArgumentType;
import org.yamcs.xtce.BooleanArgumentType;
import org.yamcs.xtce.BooleanParameterType;
import org.yamcs.xtce.DataType;
import org.yamcs.xtce.EnumeratedParameterType;
import org.yamcs.xtce.FloatParameterType;
import org.yamcs.xtce.IntegerDataEncoding;
import org.yamcs.xtce.IntegerParameterType;
import org.yamcs.xtce.Parameter;
import org.yamcs.xtce.ParameterType;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class MdbDataType {
    String name;
    Type type;
    int sizeInBits;
    ByteOrder byteOrder;
    Encoding encoding;

    public enum Encoding {
        UNSIGNED,
        SIGNED
        //TODO: add more encodings
    }

    public enum Type {
        INTEGER,
        BOOLEAN,
        ENUM,
        FLOAT,
    }

    public MdbDataType() {
        this.name = "";
        this.type = null;
        this.sizeInBits = 8;
        this.byteOrder = ByteOrder.LITTLE_ENDIAN;
        this.encoding = Encoding.UNSIGNED;
    }

    public MdbDataType(String name, Type type, int sizeInBits, ByteOrder byteOrder, Encoding encoding) {
        this.name = name;
        this.type = type;
        this.sizeInBits = sizeInBits;
        this.byteOrder = byteOrder;
        this.encoding = encoding;
    }

    public MdbDataType(String name, String type, Integer sizeInBits, String byteOrder, String encoding) throws IOException {
        this.name = name;
        this.setType(type);
        this.sizeInBits = sizeInBits;
        this.setByteOrder(byteOrder);
        this.setEncoding(encoding);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) throws IOException {
        Type newType = null;

        switch (type) {
            case "int":
                newType = Type.INTEGER;
                break;
            default:
                //TODO: custom error handling
                throw new IOException();
        }
        this.type = newType;
    }

    public Type getType() {
        return this.type;
    }

    public void setSizeInBits(int sizeInBits) {
        this.sizeInBits = sizeInBits;
    }

    public void setByteOrder(String byteOrder) {
        if (byteOrder.equals("big_endian")) {
            this.byteOrder = ByteOrder.BIG_ENDIAN;
        } else {
            this.byteOrder = ByteOrder.LITTLE_ENDIAN;
        }
    }

    public void setEncoding(String encoding) {
        if (encoding.equals("unsigned")) {
            this.encoding = Encoding.UNSIGNED;
        } else {
            this.encoding = Encoding.SIGNED;
        }
    }

    @Override
    public String toString() {
        return String.format("DataType[%s, %s, %d, %s, %s]", this.name, this.type, this.sizeInBits, this.byteOrder, this.encoding);
    }

    public ParameterType createParameterType(String name) {
        switch (this.type) {
            case INTEGER: {
                IntegerParameterType.Builder builder = new IntegerParameterType.Builder();
                builder.setName(name);
                builder.setSizeInBits(this.sizeInBits);
                IntegerDataEncoding.Builder eBuilder = new IntegerDataEncoding.Builder();
                if (this.encoding.equals(Encoding.SIGNED)) {
                    eBuilder.setEncoding(IntegerDataEncoding.Encoding.UNSIGNED);
                } else {
                    eBuilder.setEncoding(IntegerDataEncoding.Encoding.TWOS_COMPLEMENT);
                }
                eBuilder.setByteOrder(this.byteOrder);
                builder.setEncoding(eBuilder);
                return builder.build();
            }

            default: return null;
        }
    }

    public Parameter createParameter(String name) {
        Parameter param = new Parameter(name);
        param.setParameterType(this.createParameterType(name));
        return param;
    }
}
