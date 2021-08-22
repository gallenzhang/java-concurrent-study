package com.gallenzhang.dfs.namenode.server;

/**
 * @description: NameNode的RPC服务的接口
 * @className: com.gallenzhang.dfs.namenode.server.NameNodeRpcServer
 * @author: gallenzhang
 * @createDate: 2021/8/18
 */
public class NameNodeRpcServer {

    /**
     * 管理元数据的核心组件
     */
    private FSNamesystem namesystem;

    public NameNodeRpcServer(FSNamesystem namesystem) {
        this.namesystem = namesystem;
    }

    /**
     * 创建目录
     *
     * @param path
     * @return
     * @throws Exception
     */
    public Boolean mkdir(String path) throws Exception {
        return namesystem.mkdir(path);
    }

    /**
     * 启动这个rpc server
     */
    public void start() {
        System.out.println("开始监听指定的rpc server的端口号，来接收请求");
    }
}
