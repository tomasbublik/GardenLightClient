package cz.bublik.garden.lights.api

import android.util.Log
import org.apache.http.conn.util.InetAddressUtils
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor

class LightsNetworkDeviceDiscovery(private val networkController: NetworkController) {

    private val TAG = LightsNetworkDeviceDiscovery::class.java.simpleName

    private var executor = Executors.newFixedThreadPool(Const.THREADS_NUMBER)!!


    fun startIpsDiscovery() {
        networkController.getUiListener().onCleanIpsList()
        for (i in 0..Const.THREADS_NUMBER) {
            val initialRangeBorder = i * Const.RANGE_MULTIPLIER
            val rangeWidth = Const.RANGE_MULTIPLIER
            Log.d(TAG, "Starting the discovery from: " + initialRangeBorder + " to: " + initialRangeBorder.plus(rangeWidth))
            executor.submit { discoverIpRange(initialRangeBorder, initialRangeBorder.plus(rangeWidth)) }
        }
    }

    private fun getCurrentDeviceIpAddress(): String {
        var thisIp: String? = null
        try {
            val deviceInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            deviceInterfaces
                    .map { Collections.list(it.inetAddresses) }
                    .forEach {
                        it.forEach { ipAddress ->
                            if (!ipAddress.isLoopbackAddress) {
                                val upperCasedAddress = ipAddress.hostAddress.toUpperCase()
                                val isIpv4 = InetAddressUtils.isIPv4Address(upperCasedAddress)
                                if (isIpv4)
                                    thisIp = upperCasedAddress
                            }
                        }
                    }

        } catch (e: SocketException) {
            Log.e(TAG, "SocketException: " + e.message)
        }
        return thisIp!!
    }

    fun stopDiscovery() {
        Log.d(TAG, "Shutting down the discovery")
        executor.shutdownNow()
        executor = Executors.newFixedThreadPool(Const.THREADS_NUMBER)!!
    }

    fun discoveryIsActive(): Boolean {
        return (executor as ThreadPoolExecutor).activeCount != 0
    }

    private fun discoverIpRange(lowLimit: Int, highLimit: Int) {
        val ipAddress = getCurrentDeviceIpAddress()
        val subnet = ipAddress.substring(0, ipAddress.lastIndexOf("."))
        var ipToGo: String
        for (i in lowLimit..highLimit) {
            ipToGo = subnet + "." + i
            val p1 = Runtime.getRuntime().exec(Const.PING_COMMAND + ipToGo)
            val returnVal = p1.waitFor()
            val reachable = returnVal == 0
            if (reachable) {
                Log.d(TAG, "Found existing ip: " + ipToGo)
                networkController.getHostnameByIp(ipToGo)
            }
        }
    }
}