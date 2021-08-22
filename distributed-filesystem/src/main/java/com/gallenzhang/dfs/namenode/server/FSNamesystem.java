package com.gallenzhang.dfs.namenode.server;

/**
 * @description: 负责管理元数据的核心组件
 * @className: com.gallenzhang.dfs.namenode.server.FSNamesystem
 * @author: gallenzhang
 * @createDate: 2021/8/18
 */
public class FSNamesystem {

    /**
     * 负责管理内存文件目录树的组件
     */
    private FSDirectory directory;

    /**
     * 负责管理edit log写入磁盘的组件
     */
    private FSEditlog editlog;

    public FSNamesystem() {
        this.directory = new FSDirectory();
        this.editlog = new FSEditlog();
    }

    /**
     * 创建目录
     *
     * @param path
     * @return
     * @throws Exception
     */
    public Boolean mkdir(String path) throws Exception {
        directory.mkdir(path);
        editlog.logEdit("创建了一个目录：" + path);
        return true;
    }
}
