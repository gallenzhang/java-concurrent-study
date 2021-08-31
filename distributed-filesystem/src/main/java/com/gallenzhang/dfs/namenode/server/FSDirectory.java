package com.gallenzhang.dfs.namenode.server;

import java.util.LinkedList;
import java.util.List;

/**
 * @description: 负责管理内存文件中的文件目录树的核心组件
 * @className: com.gallenzhang.dfs.namenode.server.FSDirectory
 * @author: gallenzhang
 * @createDate: 2021/8/18
 */
public class FSDirectory {

    /**
     * 内存中的文件目录树
     */
    private INodeDirectory dirTree;

    public FSDirectory() {
        dirTree = new INodeDirectory("/");
    }

    /**
     * 创建目录
     *
     * @param path
     */
    public void mkdir(String path) {
        //path=/usr/warehouse/hive,先判断"/"根目录下有没有一个"usr"的目录，如果没有就创建一个"usr"目录。如果有的话，就找"/usr"目录下有没有"warehouse"目录
        //这样一层一层处理，最后创建出path目录

        synchronized (dirTree) {
            String[] paths = path.split("/");
            INodeDirectory parent = dirTree;

            for (String splitPath : paths) {
                if (splitPath.trim().equals("")) {
                    continue;
                }

                INodeDirectory dir = findDirectory(parent, splitPath);

                if (dir != null) {
                    parent = dir;
                    continue;
                }

                INodeDirectory child = new INodeDirectory(splitPath);
                parent.addChild(child);
            }
        }

    }

    /**
     * 对文件目录树递归查找目录
     *
     * @param dir
     * @param path
     * @return
     */
    private INodeDirectory findDirectory(INodeDirectory dir, String path) {
        if (dir.getChildren().size() == 0) {
            return null;
        }

        INodeDirectory resultDir;
        for (INode child : dir.getChildren()) {
            if (child instanceof INodeDirectory) {
                INodeDirectory childDir = (INodeDirectory) child;

                if (childDir.getPath().equals(path)) {
                    return childDir;
                }

                resultDir = findDirectory(childDir, path);
                if (resultDir != null) {
                    return resultDir;
                }
            }
        }

        return null;
    }

    /**
     * 代表的是文件目录树中的一个节点
     */
    private interface INode {

    }

    /**
     * 代表的是文件目录树中的一个目录
     */
    public static class INodeDirectory implements INode {

        private String path;
        private List<INode> children;

        public INodeDirectory(String path) {
            this.path = path;
            children = new LinkedList<INode>();
        }

        public void addChild(INode iNode) {
            children.add(iNode);
        }

        public void setPath(String path) {
            this.path = path;
        }

        public List<INode> getChildren() {
            return children;
        }

        public void setChildren(List<INode> children) {
            this.children = children;
        }

        public String getPath() {
            return path;
        }
    }

    /**
     * 代表文件目录树中的一个文件
     */
    public static class INodeFile implements INode {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
