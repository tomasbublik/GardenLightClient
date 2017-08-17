package lights.garden.iot.bublik.cz.gardenlight

import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import lights.garden.iot.bublik.cz.gardenlight.Const.HOSTNAME_TIMEOUT
import lights.garden.iot.bublik.cz.gardenlight.Const.REQUEST_TIMEOUT
import lights.garden.iot.bublik.cz.gardenlight.Const.STATE_TIMEOUT
import lights.garden.iot.bublik.cz.gardenlight.Utils.createHostnameRequestUrlFromGivenIp
import kotlin.text.Charsets.UTF_8

class NetworkController(url: String, private val uiListener: UiListener) : NetworkControllerInterface {

    private var networkHandler: NetworkHandler

    private val manager = FuelManager()

    init {
        changeUrl(url)
        setToggleButtonListener()
        networkHandler = NetworkHandler(uiListener, this)
    }

    fun turnLightsOn() {
        Const.TURN_LIGHTS_ON_COMMAND.httpGet().timeout(REQUEST_TIMEOUT).responseString { _, response, result ->
            networkHandler.handleCommunicationResult(result, response)
        }
    }

    fun turnLightsOff() {
        Const.TURN_LIGHTS_OFF_COMMAND.httpGet().timeout(REQUEST_TIMEOUT).responseString { _, response, result ->
            networkHandler.handleCommunicationResult(result, response)
        }
    }

    override fun checkState() {
        Const.STATE_COMMAND.httpGet().timeout(STATE_TIMEOUT).responseString(UTF_8, handleCommunicationResult(HashMap()))
    }

    override fun getHostnameByIp(ipAddress: String) {
        manager.request(Method.GET, createHostnameRequestUrlFromGivenIp(ipAddress)).timeout(HOSTNAME_TIMEOUT).responseString { _, response, result ->
            networkHandler.handleCommunicationResult(result, response)
        }
    }

    override fun handleCommunicationResult(data: Map<String, Any?>): (Request, Response, Result<String, FuelError>) -> Unit {
        return { _, response, result ->
            networkHandler.handleCommunicationResult(result, response)
        }
    }

    fun changeUrl(url: String) {
        FuelManager.instance.basePath = url
    }

    override fun setToggleButtonListener() {
        uiListener.setToggleButtonListener(this)
    }

    fun getUiListener(): UiListener {
        return uiListener
    }
}
