package org.peters.projectaws.Components.App;

import org.peters.projectaws.Components.API.Api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class App {
    List<Api> apis;

    public App() {
        this.apis = new ArrayList<>();
    }

    public App(List<Api> apis) {
        this.apis = apis;
    }

    public void setApis(Api api) {
        apis.add(api);
    }

    public void executeApi(String path, String data)  {

        Optional<Api> apiCheck = apis.stream()
                .filter(api -> api.getPath().equals(path))
                .findFirst();

        if (apiCheck.isEmpty()) return;

        Object api = apiCheck.get().getFn().execute(data);
    }
}
