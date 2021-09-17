package com.gallenzhang.register.server.cluster;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @className: com.gallenzhang.register.server.cluster.RegisterServerCluster
 * @author: gallenzhang
 * @createDate: 2021/9/17
 */
public class RegisterServerCluster {

    private static List<String> peers = new ArrayList<>();

    static {
        //读取配置文件，看看你配置了哪些机器部署了register-server
    }

    public static List<String> getPeers(){
        return peers;
    }
}
