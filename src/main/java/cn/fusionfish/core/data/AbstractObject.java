package cn.fusionfish.core.data;

import cn.fusionfish.core.utils.FileUtil;
import com.google.gson.Gson;

import java.io.File;

public abstract class AbstractObject {

    private String name;
    private transient File file;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected File getFile() {
        return this.file;
    }

    public void save() {
        String json = new Gson().toJson(this);
        FileUtil.saveJson(file,json);
    }
}
