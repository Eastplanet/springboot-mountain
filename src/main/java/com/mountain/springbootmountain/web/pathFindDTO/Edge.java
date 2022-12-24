package com.mountain.springbootmountain.web.pathFindDTO;


import lombok.Data;

import java.util.Objects;

@Data
public class Edge {
    public Pos start;
    public Pos end;

    //몇번째 Edge인지 Edge에 대한 Primary Key같은 느낌
    public long num;


    public double length;
    public Difficulty difficulty;
    public long upTime;
    public long downTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(getStart(), edge.getStart()) && Objects.equals(getEnd(), edge.getEnd());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStart(), getEnd());
    }
}
