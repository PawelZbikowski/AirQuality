package edu.ib;

public class Meta {

    private String name;
    private String license;
    private String website;
    private int page;
    private int limit;
    private int found;

    public Meta(String name, String license, String website, int page, int limit, int found) {
        this.name = name;
        this.license = license;
        this.website = website;
        this.page = page;
        this.limit = limit;
        this.found = found;
    }

    public Meta() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getFound() {
        return found;
    }

    public void setFound(int found) {
        this.found = found;
    }
}
