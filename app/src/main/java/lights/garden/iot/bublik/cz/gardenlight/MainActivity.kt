package lights.garden.iot.bublik.cz.gardenlight

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.squareup.leakcanary.LeakCanary
import lights.garden.iot.bublik.cz.gardenlight.Utils.createUrlFromIp

class MainActivity : Activity() {

    private val TAG = MainActivity::class.java.simpleName

    private lateinit var currentIpAddress: TextView
    private lateinit var scanButton: Button
    private lateinit var availableAddressesView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (LeakCanary.isInAnalyzerProcess(application)) {
            return
        }
        LeakCanary.install(application)

        val ipAddressToUse = setupIpAddress()
        currentIpAddress = findViewById(R.id.currentIpAddress) as TextView
        currentIpAddress.text = concatenatedIpAndHostname(ipAddressToUse, loadHostnameFromSharedPreferences())

        val networkController = createNetworkController(ipAddressToUse)

        val lightsNetworkDeviceDiscovery = LightsNetworkDeviceDiscovery(networkController)

        createAddressesListView(lightsNetworkDeviceDiscovery, networkController)
        createScanButton(lightsNetworkDeviceDiscovery)
    }

    private fun setupIpAddress(): String? {
        return loadIpFromSharedPreferences()
    }

    private fun createNetworkController(ipAddressToUse: String?): NetworkController {
        val communicationUIListener = CommunicationUiListener(this)
        val networkController = NetworkController(createUrlFromIp(ipAddressToUse!!), communicationUIListener)

        networkController.checkState()
        return networkController
    }

    private fun concatenatedIpAndHostname(ipAddress: String?, hostname: String?): String {
        val sb = StringBuilder()
        sb.append(ipAddress)
        if (hostname != null) {
            sb.append("($hostname)")
        }
        return sb.toString()
    }

    private fun createAddressesListView(lightsNetworkDeviceDiscovery: LightsNetworkDeviceDiscovery, networkController: NetworkController) {
        val addressesListView = findViewById(R.id.ipAdressessList) as ListView
        addressesListView.adapter = lightsNetworkDeviceDiscovery.getIpArrayAdapter()
        addressesListView.onItemClickListener = OnItemClickListener { _, _, position, id ->
            Log.d(TAG, "Clicked position: $position, id: $id")
            Log.d(TAG, "Which means this ip address: " + lightsNetworkDeviceDiscovery.getIpArrayAdapter().getItem(id.toInt()))
            lightsNetworkDeviceDiscovery.stopDiscovery()
            scanButton.setText(R.string.scan_devices)
            changeIpAddress(networkController, lightsNetworkDeviceDiscovery, id)
        }
        availableAddressesView = findViewById(R.id.availableAddressesView) as TextView
    }

    private fun changeIpAddress(networkController: NetworkController, lightsNetworkDeviceDiscovery: LightsNetworkDeviceDiscovery, id: Long) {
        val ipAddressWithHostname = lightsNetworkDeviceDiscovery.getIpArrayAdapter().getItem(id.toInt()) as Map<String, String>
        val ipAddress = ipAddressWithHostname[Const.IP_KEY]
        val hostname = ipAddressWithHostname[Const.HOSTNAME_KEY]
        concatenatedIpAndHostname(ipAddress, hostname)
        networkController.changeUrl(createUrlFromIp(ipAddress!!))
        storeToSharedPreferences(ipAddress, hostname)
        networkController.checkState()
    }

    private fun createScanButton(lightsNetworkDeviceDiscovery: LightsNetworkDeviceDiscovery) {
        scanButton = findViewById(R.id.scanButton) as Button
        scanButton.setOnClickListener({
            if (lightsNetworkDeviceDiscovery.discoveryIsActive()) {
                lightsNetworkDeviceDiscovery.stopDiscovery()
                scanButton.setText(R.string.scan_devices)
            } else {
                lightsNetworkDeviceDiscovery.startIpsDiscovery()
                scanButton.setText(R.string.stop_device_discovery)
                availableAddressesView.visibility = View.VISIBLE
            }
        })
    }

    private fun storeToSharedPreferences(ipAddress: String?, hostname: String?) {
        storeIpToSharedPreferences(getString(R.string.ip_address), ipAddress)
        storeHostnameToSharedPreferences(getString(R.string.hostname), hostname)
    }

    private fun loadIpFromSharedPreferences(): String? {
        val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val ipAddress = sharedPref.getString(getString(R.string.ip_address), resources.getString(R.string.ip_address_default))
        return ipAddress
    }

    private fun loadHostnameFromSharedPreferences(): String? {
        val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val hostname = sharedPref.getString(getString(R.string.hostname), resources.getString(R.string.hostname_default))
        return hostname
    }

    private fun storeHostnameToSharedPreferences(key: String, hostname: String?) {
        storeValueToSharedPreferences(key, hostname)
    }

    private fun storeIpToSharedPreferences(key: String, ipAddress: String?) {
        storeValueToSharedPreferences(key, ipAddress)
    }

    private fun storeValueToSharedPreferences(key: String, value: String?) {
        val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
        editor.commit()
    }
}
