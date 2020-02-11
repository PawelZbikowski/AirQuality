package edu.ib;

import java.util.List;

public class AirQualityObjects {

    private Meta meta;
    private List<Results> results;

    public AirQualityObjects(Meta meta, List<Results> results) {
        this.meta = meta;
        this.results = results;
    }

    public AirQualityObjects() {
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<Results> getResults() {
        return results;
    }

    public void setResults(List<Results> results) {
        this.results = results;
    }
}
