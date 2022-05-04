package cn.fusionfish.core.web.socket;

import cn.fusionfish.core.exception.SocketNotDeployingException;
import cn.fusionfish.core.plugin.FusionPlugin;
import cn.fusionfish.core.utils.ConsoleUtil;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author JeremyHu
 */
@SuppressWarnings("all")
public class ServerSocketThread extends Thread {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final ServerSocket serverSocket;
    private final int port;

    public ServerSocketThread() throws SocketNotDeployingException, IOException {
        FileConfiguration config = FusionPlugin.getCore().getConfig();
        this.port = config.getInt("web.socket-port");
        if (port == 0) {
            throw new SocketNotDeployingException();
        }
        serverSocket = new ServerSocket(port);
        this.start();
    }

    @Override
    public void run() {
        //开始监听
        ConsoleUtil.info("Socket服务创建成功(" + port + ").");
        while (true) {
            try {
                final Socket socket = serverSocket.accept();
                executor.execute(() -> {

                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
