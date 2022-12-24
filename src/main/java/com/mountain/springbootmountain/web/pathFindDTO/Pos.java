package com.mountain.springbootmountain.web.pathFindDTO;

import lombok.Data;

import java.util.Objects;

@Data
public class Pos {
    public Double x;
    public Double y;

    public Pos() {
    }

    public Pos(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pos pos = (Pos) o;
        return Objects.equals(x, pos.x) && Objects.equals(y, pos.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }


}
