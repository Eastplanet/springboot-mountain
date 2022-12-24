package com.mountain.springbootmountain.web.mountainDTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Data
public class Attributes {

    public long FID;
    public long PMNTN_SN;
    public String MNTN_CODE;
    public String MNTN_NM;
    public String PMNTN_NM;
    public String PMNTN_MAIN;
    public double PMNTN_LT;
    public String PMNTN_DFFL;
    public long PMNTN_UPPL;
    public long PMNTN_GODN;
    public String PMNTN_MTRQ;
    public String PMNTN_CNRL;
    public String PMNTN_CLS_;
    public String PMNTN_RISK;
    public String PMNTN_RECO;
    public String DATA_STDR_;
    public String MNTN_ID;

    public Attributes() {
    }



}
