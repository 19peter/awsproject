package org.peters.projectaws.dtos.Response.S3;


public class GetDataResponseDto implements GetResponse {
    boolean isPresent;
    String data;
    String code;

    @Override
    public boolean getIsPresent() {
        return isPresent;
    }

    public String getData() {
        return data;
    }

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setPresent(boolean present) {
        isPresent = present;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public void setIsPresent(boolean isPresent) {
        this.isPresent = isPresent;
    }
}
