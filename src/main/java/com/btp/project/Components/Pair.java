package com.btp.project.Components;

public class Pair<F, S>{
    public F first;
    public S second;

    public Pair(F first, S second){
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "{" + first + "," + second + "}";
    }
    
}
