package com.example.chaipichit_jek.a405rtdbfirebase;

/**
 * Created by FLIPFLOP on 12/10/2017.
 */

public class RecordModel {

    private String name;
    private String quote;

    public RecordModel(String name, String quote) {
        this.name = name;
        this.quote = quote;
    }

    public RecordModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }
}
