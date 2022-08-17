package org.oresat.mdb;

import java.util.List;

import org.yamcs.xtce.Argument;
import org.yamcs.xtce.ArgumentEntry;
import org.yamcs.xtce.CommandContainer;
import org.yamcs.xtce.MetaCommand;

public class MdbCommand {
    CommandContainer container;
    MetaCommand command;

    public MdbCommand(String name, List<Argument> args) {
        this.container = new CommandContainer(name);
        this.command = new MetaCommand(name);

        for (Argument arg: args) {
            this.command.addArgument(arg);
            this.container.addEntry(new ArgumentEntry(arg));
        }
    }

    public void addArgument(Argument arg) {
        this.command.addArgument(arg);
        this.container.addEntry(new ArgumentEntry(arg));
    }

    public MetaCommand build() {
        this.command.setCommandContainer(this.container);
        return this.command;
    }
}
