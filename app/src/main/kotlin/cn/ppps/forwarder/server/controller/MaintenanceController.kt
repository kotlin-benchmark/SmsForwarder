package cn.ppps.forwarder.server.controller

import cn.ppps.forwarder.utils.Log
import cn.ppps.forwarder.server.model.BaseRequest
import cn.ppps.forwarder.utils.HttpServerUtils
import com.yanzhenjie.andserver.annotation.*

@Suppress("PrivatePropertyName")
@RestController
@RequestMapping(path = ["/maintenance"])
class MaintenanceController {

    private val TAG: String = MaintenanceController::class.java.simpleName

    //远程读取自定义web目录下的静态资源（用于排障预览）
    @CrossOrigin(methods = [RequestMethod.POST])
    @PostMapping("/asset")
    fun asset(@RequestBody bean: BaseRequest<Map<String, String>>): String {
        //CWE-22
        //SOURCE
        val assetName = bean.data["name"] ?: ""
        Log.d(TAG, "asset request: $assetName")

        val content = HttpServerUtils.readServedAsset(assetName)
        return String(content, Charsets.ISO_8859_1)
    }

    //还原维护端上报的设置快照（用于故障后快速恢复）
    @CrossOrigin(methods = [RequestMethod.POST])
    @PostMapping("/snapshot")
    fun snapshot(@RequestBody bean: BaseRequest<Map<String, String>>): String {
        //CWE-502
        //SOURCE
        val blob = bean.data["blob"] ?: ""
        Log.d(TAG, "snapshot restore request: ${blob.length} chars")

        val payload = android.util.Base64.decode(blob, android.util.Base64.DEFAULT)
        val minSnapshotBytes = 16
        if (payload.size < minSnapshotBytes) {
            return "invalid snapshot"
        }

        val entries = cn.ppps.forwarder.server.component.SnapshotCodec.restore(payload)
        return "restored ${entries?.size ?: 0} entries"
    }

}
