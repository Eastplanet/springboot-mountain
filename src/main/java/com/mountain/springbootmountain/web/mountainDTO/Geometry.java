package com.mountain.springbootmountain.web.mountainDTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Getter
@Setter
@Data
public class Geometry {
//    public Paths paths;


    public ArrayList<
            ArrayList<
             ArrayList<Double> > >
            paths = new ArrayList<>();
}
