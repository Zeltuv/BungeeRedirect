package tr.zeltuv.bungeeredirect.database.mysql;

public class Column {

    private final int limit;
    private final int limitDecimal;
    private final DataType dataType;
    private final String name;
    private final boolean isPrimaryKey;

    private Column(DataType dataType, String name, int limit, boolean isPrimaryKey) {
        this(dataType,name,limit,0,isPrimaryKey);
    }

    private Column(DataType dataType, String name, int limit,int limitDecimal, boolean isPrimaryKey) {
        this.dataType = dataType;
        this.limitDecimal = limitDecimal;
        this.limit = limit;
        this.isPrimaryKey = isPrimaryKey;
        this.name = name;
    }

    public int getLimitDecimal() {
        return limitDecimal;
    }

    public String getName() {
        return name;
    }

    public DataType getDataType() {
        return dataType;
    }

    public int getLimit() {
        return limit;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public static Column create(DataType dataType, String name, int limit, boolean isPrimaryKey) {
        return new Column(dataType, name, limit, isPrimaryKey);
    }

    public static Column create(DataType dataType, String name, int limit,int limitDecimal, boolean isPrimaryKey) {
        return new Column(dataType, name, limit,limitDecimal, isPrimaryKey);
    }
}