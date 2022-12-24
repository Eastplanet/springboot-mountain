package com.mountain.springbootmountain.web.mountainDTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@Data
public class Mountain {

    public String displayFieldName;
    //딱히 필요없는 정보 인 것 같아서 스트링으로함
    public FieldAliases fieldAliases;
    public String geometryType;
    public SpatialReference spatialReference;
    public ArrayList<Field> fields = new ArrayList<>();
//    public Features features;
    public ArrayList<Feature> features = new ArrayList<>();

    public Mountain() {
    }


}
