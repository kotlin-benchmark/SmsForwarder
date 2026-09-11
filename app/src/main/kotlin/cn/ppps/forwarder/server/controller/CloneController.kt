package cn.ppps.forwarder.server.controller

import cn.ppps.forwarder.utils.Log
import cn.ppps.forwarder.R
import cn.ppps.forwarder.entity.CloneInfo
import cn.ppps.forwarder.server.model.BaseRequest
import cn.ppps.forwarder.utils.HttpServerUtils
import com.xuexiang.xutil.resource.ResUtils.getString
import com.yanzhenjie.andserver.annotation.*

@Suppress("PrivatePropertyName")
@RestController
@RequestMapping(path = ["/clone"])
class CloneController {

    private val TAG: String = CloneController::class.java.simpleName

    //客户端从服务端拉取克隆信息
    @CrossOrigin(methods = [RequestMethod.POST])
    @PostMapping("/pull")
    fun pull(@RequestBody bean: BaseRequest<CloneInfo>): CloneInfo {
        val cloneBean = bean.data
        Log.d(TAG, cloneBean.toString())

        HttpServerUtils.compareVersion(cloneBean)

        val cloneInfo = HttpServerUtils.exportSettings()
        Log.d(TAG, cloneInfo.toString())
        return cloneInfo
    }

    //客户端向服务端推送克隆信息
    @CrossOrigin(methods = [RequestMethod.POST])
    @PostMapping("/push")
    fun push(@RequestBody bean: BaseRequest<CloneInfo>): String {
        val cloneInfo = bean.data
        Log.d(TAG, cloneInfo.toString())

        HttpServerUtils.compareVersion(cloneInfo)

        return if (HttpServerUtils.restoreSettings(cloneInfo)) "success" else getString(R.string.restore_failed)
    }

    //预览转发规则：用给定的规则表达式测试样本文本是否命中（保存规则前先自检）
    @CrossOrigin(methods = [RequestMethod.POST])
    @PostMapping("/rule-preview")
    fun rulePreview(@RequestBody bean: BaseRequest<Map<String, String>>): String {
        //CWE-1333
        //SOURCE
        val pattern = bean.data["pattern"] ?: ""
        val sample = bean.data["sample"] ?: ""
        Log.d(TAG, "rule preview request: ${pattern.length} chars")

        val matched = cn.ppps.forwarder.utils.RuleLine.previewCondition(pattern, sample)
        return if (matched) "matched" else "no match"
    }

}