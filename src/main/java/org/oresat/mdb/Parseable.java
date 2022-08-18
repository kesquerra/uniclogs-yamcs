package org.oresat.mdb;


//TODO: update to Mdb Specific interface or abstract class
public interface Parseable {
    public void setField(String name, String value) throws ReflectiveOperationException;
}
