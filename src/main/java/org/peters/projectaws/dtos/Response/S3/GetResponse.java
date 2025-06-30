package org.peters.projectaws.dtos.Response.S3;

import org.peters.projectaws.dtos.Response.Response;

public interface GetResponse extends Response {
    public String getData();
    public boolean getIsPresent();

    public void setData(String data);
    public void setIsPresent(boolean isPresent);

}
