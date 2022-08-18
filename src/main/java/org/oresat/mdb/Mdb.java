package org.oresat.mdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.yamcs.xtce.SpaceSystem;

public class Mdb {
    HashMap<String, MdbDataType> dataTypes;
    HashMap<String, MdbParameter> parameters;
    HashMap<String, MdbCommand> commands;

    public Mdb() {
        this.dataTypes = new HashMap<>();
        this.parameters = new HashMap<>();
        this.commands = new HashMap<>();
    }

    public void addDataType(MdbDataType dataType) {
        this.dataTypes.put(dataType.getName(), dataType);
    }

    public void addParameter(MdbParameter parameter) {
        this.parameters.put(parameter.getName(), parameter);
    }

    public void addCommand(MdbCommand command) {
        this.commands.put(command.getName(), command);
    }

    public void setDataTypes(List<MdbDataType> dataTypes) {
        for (MdbDataType dataType: dataTypes) {
            this.addDataType(dataType);
        }
    }

    public void setParameters(List<MdbParameter> parameters) {
        for (MdbParameter param: parameters) {
            this.addParameter(param);
        }
    }

    public void setCommands(List<MdbCommand> commands) {
        for (MdbCommand cmd: commands) {
            this.addCommand(cmd);
        }
    }

    public MdbDataType getDataType(String name) {
        MdbDataType dType = this.dataTypes.get(name);
        if (dType == null) {
            //TODO: add custom error handling for missing datatype
        }
        return dType;
    }

    public Map<String, MdbDataType> getDataTypeMap() {
        return this.dataTypes;
    }

     public SpaceSystem migrateToSpaceSystem(SpaceSystem system) {
        for (MdbDataType dt: this.dataTypes.values()) {
            System.err.println(dt.toString());
            system.addParameterType(dt.createParameterType());
        }
        for (MdbParameter p: this.parameters.values()) {
            System.err.println(p.toString());
            system.addParameter(p.createParameter());
        }
        for (MdbCommand c: this.commands.values()) {
            System.err.println(c.toString());
            system.addMetaCommand(c.createMetaCommand());
        }
        return system;
    }
}
