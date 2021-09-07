package com.gallenzhang.dfs.datanode.server;

/**
 * @description: DataNode启动类
 * @className: com.gallenzhang.dfs.datanode.server.DataNode
 * @author: gallenzhang
 * @createDate: 2021/9/7
 */
public class DataNode {

    /**
     * 是否还在运行
     */
    private volatile Boolean shouldRun;

    /**
     * 负责跟一组NameNode通信的组件
     */
    private NameNodeGroupOfferService offerService;

    /**
     * 初始化DataNode
     */
    private void initialize() {
        this.shouldRun = true;
        this.offerService = new NameNodeGroupOfferService();
        this.offerService.start();
    }

    /**
     * 运行DataNode
     */
    private void run() {
        try {
            while (shouldRun) {
                Thread.sleep(10000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DataNode dataNode = new DataNode();
        dataNode.initialize();
        dataNode.run();
    }
}
