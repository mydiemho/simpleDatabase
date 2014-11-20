interface Database {
    public void set(String name, String value);

    public String get(String name);

    public void unset(String name);

    public int numEqualTo(String value);

    public void beginTransaction();

    public boolean commit();

    public boolean rollBack();
}
