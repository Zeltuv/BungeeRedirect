package tr.zeltuv.bungeeredirect.database.mysql;

public class DatabaseValue {

    private final String name;
    private final Object value;

    private DatabaseValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
    public String getName() {
        return name;
    }

    public static DatabaseValue get(String name,Object value){
        return new DatabaseValue(name,value);
    }
}