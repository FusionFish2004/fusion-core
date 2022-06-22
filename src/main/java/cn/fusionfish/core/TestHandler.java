package cn.fusionfish.core;

import cn.fusionfish.core.annotations.RequestHandler;
import cn.fusionfish.core.annotations.RequestParam;
import cn.fusionfish.core.web.http.Handler;

/**
 * @author JeremyHu
 */
@RequestHandler(path = "/api")
public class TestHandler implements Handler {
    @RequestHandler(path = "/double")
    public String testDouble(
            @RequestParam(defaultValue = "233", paramName = "num") double n
    ) {
        return String.valueOf(n);
    }

    @RequestHandler(path = "/string")
    public String testString(
            @RequestParam(defaultValue = "default", paramName = "str") String str
    ) {
        return str;
    }
}
