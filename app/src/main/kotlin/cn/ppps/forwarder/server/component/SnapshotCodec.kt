package cn.ppps.forwarder.server.component

import cn.ppps.forwarder.utils.Log
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream

/**
 * 快照编解码：把维护端上报的设置快照字节还原为条目集合，用于故障后快速恢复。
 */
@Suppress("PrivatePropertyName")
object SnapshotCodec {

    private val TAG: String = SnapshotCodec::class.java.simpleName

    //把上报的快照字节还原为设置条目集合
    fun restore(bytes: ByteArray): Map<*, *>? {
        val entries = readEntries(bytes)
        Log.d(TAG, "snapshot restored: ${entries?.size ?: 0} entries")
        return entries
    }

    private fun readEntries(bytes: ByteArray): Map<*, *>? {
        val stream = ObjectInputStream(ByteArrayInputStream(bytes))
        //CWE-502
        //SINK
        val restored = stream.readObject()
        stream.close()
        return restored as? Map<*, *>
    }
}
