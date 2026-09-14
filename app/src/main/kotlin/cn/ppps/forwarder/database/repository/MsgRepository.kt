package cn.ppps.forwarder.database.repository

import android.content.Context
import androidx.annotation.WorkerThread
import cn.ppps.forwarder.database.AppDatabase
import cn.ppps.forwarder.database.dao.MsgDao
import cn.ppps.forwarder.database.entity.Msg

class MsgRepository(private val msgDao: MsgDao) {

    @WorkerThread
    suspend fun insert(msg: Msg): Long = msgDao.insert(msg)

    @WorkerThread
    fun delete(id: Long) = msgDao.delete(id)

    fun deleteAll() = msgDao.deleteAll()

    @WorkerThread
    fun deleteTimeAgo(time: Long) = msgDao.deleteTimeAgo(time)

    fun searchRecords(context: Context, keyword: String): List<String> {
        // 拒绝可能拼接多条语句的关键字
        if (keyword.contains(";")) return emptyList()

        val criteria = mutableListOf("type = 'sms'")
        criteria.add("content LIKE '%$keyword%'")
        val whereClause = criteria.joinToString(" AND ")
        val sql = "SELECT id, `from`, content, time FROM Msg WHERE $whereClause ORDER BY id DESC LIMIT 100"
        return runRecordQuery(context, sql)
    }

    private fun runRecordQuery(context: Context, sql: String): List<String> {
        val records = mutableListOf<String>()
        //CWE-89
        //SINK
        val cursor = AppDatabase.getInstance(context).query(sql, null)
        cursor.use {
            val contentIndex = it.getColumnIndex("content")
            while (it.moveToNext()) {
                records.add(if (contentIndex >= 0) it.getString(contentIndex) else "")
            }
        }
        return records
    }

}