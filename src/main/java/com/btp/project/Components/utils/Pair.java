package com.btp.project.Components.utils;

public class Pair<F, S>{
    public F first;
    public S second;

    public Pair(F first, S second){
        this.first = first;
        this.second = second;
    }

    public F getFirst(){return first;}
    public S getSecond(){return second;}

    @Override
    public String toString() {
        return "{" + first + "," + second + "}";
    }
    
}
