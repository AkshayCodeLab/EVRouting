package com.btp.project.Components.utils;

public class Link {
    private Integer source;
    private Integer target;
    private Integer value;

    public Link(Integer source,Integer target, Integer value){
        this.source = source;
        this.target = target;
        this.value = value;
    }

    public Integer getSource() {return source;}
    public Integer getTarget() {return target;}
    public Integer getValue() {return value;}
    @Override
    public String toString() {
        return String.format( "['%d', '%d', '%d']", source, target, value);
    }
}
