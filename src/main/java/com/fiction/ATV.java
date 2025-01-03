package com.fiction;

public class ATV {
    private String atvId;
    private String modelName;
    private boolean availability;

    public ATV(String atvId, String modelName, boolean availability) {
        this.atvId = atvId;
        this.modelName = modelName;
        this.availability = availability;
    }

    public String getAtvId() {
        return atvId;
    }

    public String getModelName() {
        return modelName;
    }

    public boolean isAvailable() {
        return availability;
    }
}