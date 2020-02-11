package edu.ib;

public class Date {

    private String local;
    private String utc;

    public Date(String local, String utc) {
        this.local = local;
        this.utc = utc;
    }

    public Date() {
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getUtc() {
        return utc;
    }

    public void setUtc(String utc) {
        this.utc = utc;
    }

    @Override
    public String toString() {
        return "Date{" +
                "local='" + local + '\'' +
                ", utc='" + utc + '\'' +
                '}';
    }
}
