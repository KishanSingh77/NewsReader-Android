package com.example.newsreader;

import java.io.Serializable;

public class Source implements Serializable {
    @Override
    public String toString() {
        return "Source{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public  String id ;
    public  String name ;


}
