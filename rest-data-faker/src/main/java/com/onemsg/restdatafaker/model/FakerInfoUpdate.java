package com.onemsg.restdatafaker.model;

import com.onemsg.restdatafaker.Delay;
import com.onemsg.restdatafaker.FakerType;

import io.vertx.core.json.JsonObject;

public class FakerInfoUpdate {
    
    public String path;
    public FakerType type;
    public String name;
    public String description;
    public JsonObject expression;
    public String locale;
    public Delay delay;
}
