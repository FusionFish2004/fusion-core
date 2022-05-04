package cn.fusionfish.core;

import cn.fusionfish.core.plugin.FusionPlugin;
import cn.fusionfish.core.web.http.ServerController;

import static cn.fusionfish.core.utils.ConsoleUtil.info;

/**
 * @author JeremyHu
 */
public final class FusionCore extends FusionPlugin {
    @Override
    protected void enable() {

    }

    @Override
    protected void disable() {
        ServerController serverController = getServerController();
        if (serverController != null) {
            info("正在关闭HTTP服务器...");
            serverController.stop();
        }
    }
}
