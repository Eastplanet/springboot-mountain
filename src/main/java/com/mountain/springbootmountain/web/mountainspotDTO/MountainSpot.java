package com.mountain.springbootmountain.web.mountainspotDTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@Data
public class MountainSpot {

    public String displayFieldName;
    public FieldAliases fieldAliases;
    public String geometryType;
    public SpatialReference spatialReference;
    public ArrayList<Field> fields = new ArrayList<>();
    public ArrayList<Feature> features = new ArrayList<>();


}
