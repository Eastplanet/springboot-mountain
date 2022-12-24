package com.mountain.springbootmountain.web.mountainDTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Getter
@Setter
@Data
public class Paths {
    public ArrayList<Path> path = new ArrayList<Path>();
}
