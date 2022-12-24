package com.mountain.springbootmountain.web.pathFindDTO;

public class PqEle implements Comparable<PqEle>{
    public int idx;
    public double dist;

    public PqEle() {
    }

    public PqEle(int idx, double dist) {
        this.idx = idx;
        this.dist = dist;
    }

    @Override
    public int compareTo(PqEle o) {
        if(this.dist < o.dist)
            return 1;
        else if(this.dist > o.dist)
            return -1;
        else return 0;
    }
}
