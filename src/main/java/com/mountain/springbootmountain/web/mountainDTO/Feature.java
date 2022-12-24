package com.mountain.springbootmountain.web.mountainDTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Feature {

    public Attributes attributes;
    public Geometry geometry;
}
