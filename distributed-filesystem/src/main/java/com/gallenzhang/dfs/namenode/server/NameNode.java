package com.gallenzhang.dfs.namenode.server;

/**
 * @description: NameNode 核心启动类
 * @className: com.gallenzhang.dfs.namenode.server.NameNode
 * @author: gallenzhang
 * @createDate: 2021/8/18
 */
public class NameNode {

    /**
     * NameNode是否在运行
     */
    private volatile Boolean shouldRun;

    /**
     * 负责管理元数据的核心组件
     */
    private FSNamesystem namesystem;

    /**
     * NameNode对外提供rpc接口的server，可以响应请求
     */
    private NameNodeRpcServer rpcServer;


    public NameNode() {
        this.shouldRun = true;
    }

    private void initialize() {
        this.namesystem = new FSNamesystem();
        this.rpcServer = new NameNodeRpcServer(this.namesystem);
        this.rpcServer.start();
    }

    /**
     * 让NameNode运行起来
     */
    private void run() {
        try {
            while (shouldRun) {
                Thread.sleep(10000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        NameNode nameNode = new NameNode();
        nameNode.initialize();
        nameNode.run();
    }
}
