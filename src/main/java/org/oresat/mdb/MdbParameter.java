package org.oresat.mdb;

import org.yamcs.xtce.Parameter;

public class MdbParameter implements Parseable {
    String name;
    MdbDataType datatype;
    String description;

    public MdbParameter() {
        this.name = "";
        this.datatype = null;
        this.description = "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public void setDataType(MdbDataType dataType) {
        this.datatype = dataType;
    }

    public String getName() {
        return this.name;
    }

    public Parameter createParameter() {
        Parameter param = new Parameter(name);

        //TODO: fix duplication of parameter type creation
        param.setParameterType(this.datatype.createParameterType());
        return param;
    }

    public void setField(String name, String value) throws ReflectiveOperationException {
        String methodName = "set" + name.substring(0,1).toUpperCase() + name.substring(1);
        System.err.println(methodName + " " + value);
        this.getClass().getDeclaredMethod(methodName, String.class).invoke(this, value);
    }

    @Override
    public String toString() {
        return String.format("MdbParameter[%s, %s, %s]", this.name, this.datatype.getName(), this.description);
    }
}
