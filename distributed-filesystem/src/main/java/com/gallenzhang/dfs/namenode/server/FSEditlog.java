package com.gallenzhang.dfs.namenode.server;


import java.util.LinkedList;

/**
 * @description:
 * @className: com.gallenzhang.dfs.namenode.server.FSEditlog
 * @author: gallenzhang
 * @createDate: 2021/8/18
 */
public class FSEditlog {


    /**
     * 当前递增到的txid序号
     */
    private long txidSeq = 0;

    /**
     * 内存双缓冲区
     */
    private DoubleBuffer editLogBuffer = new DoubleBuffer();

    /**
     * 当前是否在将内存缓冲刷入磁盘中
     */
    private volatile Boolean isSyncRunning = false;

    /**
     * 当前是否有线程在等待刷新下一批edit log到磁盘里去
     */
    private volatile Boolean isWaitSync = false;

    /**
     * 在同步到磁盘中的最大的一个txid
     */
    private volatile Long syncMaxTxid = 0L;

    /**
     * 每个线程自己本地的txid副本
     */
    private ThreadLocal<Long> localTxid = new ThreadLocal<Long>();


    /**
     * 记录edit log日志
     *
     * @param content
     */
    public void logEdit(String content) {
        //将日志写入缓冲区
        synchronized (this) {
            //获取全局唯一递增的txid，代表了edit log的序号
            long txid = ++txidSeq;
            localTxid.set(txid);

            //构造一条edit log对象
            EditLog log = new EditLog(txid, content);

            //将edit log写入内存缓冲中，不是直接刷入磁盘文件
            editLogBuffer.write(log);
        }

        //将缓冲区中的数据刷入磁盘文件中
        logSync();
    }

    /**
     * 将内存缓冲中的数据刷入磁盘文件中
     * 在这里尝试允许某一个线程一次性将内存缓冲中的数据刷入磁盘文件中
     * 相当于实现一个批量将内存缓冲数据刷磁盘的过程
     */
    private void logSync() {
        //再次尝试加锁
        synchronized (this) {
            //如果说当前正好有线程在刷内存缓冲到磁盘中去
            if (isSyncRunning) {

                //那么此时这里应该有一些逻辑判断，假如说某个线程已经把txid=1,2,3,4,5的edit log都从syncBuffer刷入磁盘了，或者说此时正在刷入磁盘中
                //此时syncMaxTxid=5,代表的是正在刷入磁盘的最大txid
                //那么这时候来一个线程，他对应的txid=3，此时他是可以直接返回了，就代表说肯定是他对应的edit log已经被别的线程在刷入磁盘了，这个时候txid=3的线程就不需要等待了
                long txid = localTxid.get();
                if (txid <= syncMaxTxid) {
                    return;
                }

                //此时再来一个txid=9的线程的话，那么会发现已经有线程在等待下一批数据刷入到磁盘了，此时他会直接返回
                //假如说此时来了一个txid=6的线程，那就不好说了，他需要做一些等待，同时要释放掉锁
                if (isWaitSync) {
                    return;
                }

                //比如说此时可能是txid=15的线程在这里等待
                isWaitSync = true;
                while (isSyncRunning) {
                    try {
                        wait(2000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                isWaitSync = false;
            }

            //交换两块缓冲区
            editLogBuffer.setReadyToSync();

            //然后就可以保存一下当前要同步到磁盘中去的最大的txid，此时editLogBuffer中的syncBuffer这块区域，交换完以后可能有多条数据
            //而且他里面的edit log的txid一定是从小到大的，此时要同步的txid=6,7,8,9,10,11,12 syncMaxTxid=12
            syncMaxTxid = editLogBuffer.getSyncMaxTxid();

            //设置当前正在同步到磁盘的标志位
            isSyncRunning = true;
        }

        //开始同步内存缓冲的数据到磁盘文件里去，这个过程其实是比较慢，基本上肯定是毫秒级了，弄不好要几十毫秒。耗时的磁盘操作没有加锁。
        editLogBuffer.flush();

        synchronized (this) {
            //同步完了磁盘之后，就会将标志位复位，再释放锁
            isSyncRunning = false;

            //唤醒可能正在等待他同步完磁盘的线程
            notifyAll();
        }
    }

    /**
     * 代表了一条edit log
     */
    class EditLog {

        long txid;
        String content;

        public EditLog(long txid, String content) {
            this.txid = txid;
            this.content = content;
        }

        @Override
        public String toString() {
            return "EditLog{" +
                    "txid=" + txid +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

    /**
     * 内存双缓冲
     */
    class DoubleBuffer {
        /**
         * 专门用来承载线程写入edit log
         */
        LinkedList<EditLog> currentBuffer = new LinkedList<EditLog>();

        /**
         * 专门用来将数据同步到磁盘中去的一块缓存
         */
        LinkedList<EditLog> syncBuffer = new LinkedList<EditLog>();

        /**
         * 将edit log写到内存缓冲里去
         *
         * @param log
         */
        public void write(EditLog log) {
            currentBuffer.add(log);
        }

        /**
         * 交换两块缓冲区，为了同步内存数据到磁盘做准备
         */
        public void setReadyToSync() {
            LinkedList<EditLog> tmp = currentBuffer;
            currentBuffer = syncBuffer;
            syncBuffer = tmp;
        }

        /**
         * 获取sync buffer缓冲区里的最大的一个txid
         *
         * @return
         */
        public Long getSyncMaxTxid() {
            return syncBuffer.getLast().txid;
        }

        /**
         * 将syncBuffer缓冲区中的数据刷入到磁盘中
         */
        public void flush() {
            for (EditLog log : syncBuffer) {
                System.out.println("将edit log写入磁盘文件中：" + log);

                //正常来说，就是用文件输出流将数据写入磁盘文件中
            }

            syncBuffer.clear();
        }

    }
}
