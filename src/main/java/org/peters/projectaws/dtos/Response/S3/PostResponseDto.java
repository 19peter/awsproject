package org.peters.projectaws.dtos.Response.S3;

public class PostResponseDto implements PostResponse {

    String code;

    public PostResponseDto(String code) {
        this.code = code;
    }


    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }
}
