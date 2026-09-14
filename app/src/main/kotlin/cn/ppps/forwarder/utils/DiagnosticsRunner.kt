package cn.ppps.forwarder.utils

/**
 * Runs on-device network diagnostics on behalf of the remote command channel.
 * The probe recipe is executed through the system shell so that multi-step
 * diagnostics (reachability checks, route dumps) can be expressed as one line.
 */
object DiagnosticsRunner {

    private const val TAG = "DiagnosticsRunner"

    fun runNetworkDiagnostic(command: List<String>): Boolean {
        val builder = ProcessBuilder()
        builder.redirectErrorStream(true)
        //CWE-78
        //SINK
        val process = builder.command(command).start()
        val output = process.inputStream.bufferedReader().use { it.readText() }
        Log.d(TAG, "network diagnostic output: $output")
        process.waitFor()
        return true
    }
}
