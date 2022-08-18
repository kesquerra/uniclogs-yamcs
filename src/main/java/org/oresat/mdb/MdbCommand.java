package org.oresat.mdb;

import java.util.List;

import org.yamcs.xtce.Argument;
import org.yamcs.xtce.ArgumentEntry;
import org.yamcs.xtce.CommandContainer;
import org.yamcs.xtce.MetaCommand;

public class MdbCommand implements Parseable {
    String name;
    CommandContainer container;
    MetaCommand command;

    public MdbCommand(String name, List<Argument> args) {
        this.name = name;
        this.container = new CommandContainer(name);
        this.command = new MetaCommand(name);

        for (Argument arg: args) {
            this.command.addArgument(arg);
            this.container.addEntry(new ArgumentEntry(arg));
        }
    }

    public String getName() {
        return this.name;
    }

    public void addArgument(Argument arg) {
        this.command.addArgument(arg);
        this.container.addEntry(new ArgumentEntry(arg));
    }

    public MetaCommand createMetaCommand() {
        this.command.setCommandContainer(this.container);
        return this.command;
    }

    public void setField(String name, String value) throws ReflectiveOperationException {
        String methodName = "set" + name.substring(0,1).toUpperCase() + name.substring(1);
        System.err.println(methodName + " " + value);
        this.getClass().getDeclaredMethod(methodName, String.class).invoke(this, value);
    }
}
