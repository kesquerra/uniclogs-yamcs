package org.oresat.mdb;

import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.yamcs.ConfigurationException;
import org.yamcs.YConfiguration;
import org.yamcs.protobuf.Mdb;
import org.yamcs.xtce.Argument;
import org.yamcs.xtce.DatabaseLoadException;
import org.yamcs.xtce.SpaceSystem;
import org.yamcs.xtce.SpaceSystemLoader;

public class MdbLoader implements SpaceSystemLoader {
    String name;
    String file;

    public MdbLoader(YConfiguration config) {
        this.name = config.getString("name");
        this.file = config.getString("file");
    }

    @Override
    public SpaceSystem load() throws ConfigurationException, DatabaseLoadException {
        SpaceSystem system = new SpaceSystem(this.name);
        ArrayList<Argument> args = new ArrayList<>();

        /***for (Integer i: new Integer[]{2,4,8}) {
            IntParameter param = new IntParameter("uint" + i.toString(), i);
            system.addArgumentType(param.getArgType());
            system.addParameterType(param.getType());
            system.addParameter(param.getParameter("uint" + i.toString()));
            args.add(param.getArgument("uint" + i.toString()));
        }
        MdbCommand cmd = new MdbCommand("test", args);
        system.addMetaCommand(cmd.build());**/
        org.oresat.mdb.Mdb mdb = this.getMdb(this.file);

        system = mdb.migrateToSpaceSystem(system);

        return system;
    }

    private org.oresat.mdb.Mdb getMdb(String file) throws ConfigurationException {
        try {
            MdbParser parser = new MdbParser(file);
            return parser.parse();
        } catch (IOException | ReflectiveOperationException e) {
            throw new ConfigurationException(e);
        }

    } 

    @Override
    public String getConfigName() {
        return this.name;
    }

    @Override
    public boolean needsUpdate(RandomAccessFile consistencyDateFile) throws IOException, ConfigurationException {
        return false;
    }
    @Override
    public void writeConsistencyDate(FileWriter consistencyDateFile) throws IOException {
        return;
    }
}
