package org.oresat.mdb;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteOrder;

import org.yamcs.xtce.Argument;
import org.yamcs.xtce.ArgumentType;
import org.yamcs.xtce.BooleanArgumentType;
import org.yamcs.xtce.BooleanDataEncoding;
import org.yamcs.xtce.BooleanParameterType;
import org.yamcs.xtce.DataType;
import org.yamcs.xtce.EnumeratedParameterType;
import org.yamcs.xtce.FloatParameterType;
import org.yamcs.xtce.IntegerDataEncoding;
import org.yamcs.xtce.IntegerParameterType;
import org.yamcs.xtce.Parameter;
import org.yamcs.xtce.ParameterType;

public class MdbDataType implements Parseable {
    public String name;
    public Type type;
    public Integer sizeInBits;
    public ByteOrder byteOrder;
    public Encoding encoding;

    private enum Encoding {
        UNSIGNED,
        SIGNED
        //TODO: add more encodings
    }

    private enum Type {
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

    public String getName() {
        return this.name;
    }

    public void setType(String type) {
        if (type.equals("int")) {
            this.type = Type.INTEGER;
        } else if (type.equals("bool")) {
            this.type = Type.BOOLEAN;
        } else if (type.equals("enum")) {
            this.type = Type.ENUM;
        } else {
            this.type = Type.FLOAT;
        }

        //TODO: Error handling for unknown type
    }

    public Type getType() {
        return this.type;
    }

    public void setSizeInBits(int sizeInBits) {
        this.sizeInBits = sizeInBits;
    }

    public void setSizeInBits(String sizeInBits) {
        this.sizeInBits = Integer.parseInt(sizeInBits);
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
        return String.format("MdbDataType[%s, %s, %d, %s, %s]", this.name, this.type, this.sizeInBits, this.byteOrder, this.encoding);
    }

    public ParameterType createParameterType() {
        switch (this.type) {
            case INTEGER: {
                IntegerParameterType.Builder builder = new IntegerParameterType.Builder();
                builder.setName(name);
                builder.setSizeInBits(this.sizeInBits);
                IntegerDataEncoding.Builder eBuilder = new IntegerDataEncoding.Builder();
                if (this.encoding.equals(Encoding.UNSIGNED)) {
                    eBuilder.setEncoding(IntegerDataEncoding.Encoding.UNSIGNED);
                } else {
                    eBuilder.setEncoding(IntegerDataEncoding.Encoding.TWOS_COMPLEMENT);
                }
                eBuilder.setByteOrder(this.byteOrder);
                builder.setEncoding(eBuilder);
                return builder.build();
            }
            case BOOLEAN: {
                BooleanParameterType.Builder builder = new BooleanParameterType.Builder();
                BooleanDataEncoding.Builder eBuilder = new BooleanDataEncoding.Builder();
                builder.setName(name);
                eBuilder.setSizeInBits(this.sizeInBits);
                builder.setEncoding(eBuilder);
                return builder.build();
            }

            default: return null;
        }
    }


    public void setField(String name, String value) throws ReflectiveOperationException {
        String methodName = "set" + name.substring(0,1).toUpperCase() + name.substring(1);
        System.err.println(methodName + " " + value);
        this.getClass().getDeclaredMethod(methodName, String.class).invoke(this, value);
    }
}
