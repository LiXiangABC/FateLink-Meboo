package com.crush.view.view;

public enum DateSelectStyle {
    MM_DD_YYYY("MM/DD/YYYY"),
    MMDDYYYY("MM-DD-YYYY");
   public String valus;

    DateSelectStyle(String s) {
        this.valus = s;
    }
}
