package lights.garden.iot.bublik.cz.gardenlight

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result

 class FakeNetworkController(val response: Response, uiListener: UiListener) : NetworkControllerInterface {

    override fun getHostnameByIp(ipAddress: String) {
        val result = Result.Success<String, FuelError>(response.httpResponseMessage) as Result<String, FuelError>
        handleCommunicationResult(mapOf("response" to "0")).invoke(Request(), response, result)
    }

    private var networkHandler: NetworkHandler = NetworkHandler(uiListener, this)

    override fun checkState() {
        val result = Result.Success<String, FuelError>(response.httpResponseMessage) as Result<String, FuelError>
        handleCommunicationResult(mapOf("response" to "0")).invoke(Request(), response, result)
    }

    override fun setToggleButtonListener() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleCommunicationResult(data: Map<String, Any?>): (Request, Response, Result<String, FuelError>) -> Unit {
        return { _, response, result ->
            networkHandler.handleCommunicationResult(result, response)
        }
    }
}