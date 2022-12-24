package com.mountain.springbootmountain.web.mountainDTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Field {
    public String name;
    public String type;
    public String alias;
    public long length;

    public Field() {
    }
}
