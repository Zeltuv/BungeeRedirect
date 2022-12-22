package tr.zeltuv.bungeeredirect.database.mysql;

public enum DataType {

    VARCHAR("VARCHAR"),TIMESTAMP("TIMESTAMP"),TEXT("TEXT"),BOOLEAN("BOOLEAN"),DOUBLE("DOUBLE"),INT("INT");

    private final String name;

    DataType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}