package lights.garden.iot.bublik.cz.gardenlight

import android.util.Log
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import lights.garden.iot.bublik.cz.gardenlight.Const.HOSTNAME_COMMAND
import lights.garden.iot.bublik.cz.gardenlight.Const.HOSTNAME_KEY
import lights.garden.iot.bublik.cz.gardenlight.Const.IP_KEY
import lights.garden.iot.bublik.cz.gardenlight.Const.PORT
import lights.garden.iot.bublik.cz.gardenlight.Const.POSITIVE_RESPONSE
import lights.garden.iot.bublik.cz.gardenlight.Const.STATE_ACTIVATED_RESPONSE
import java.util.*

class NetworkHandler(private val uiListener: UiListener, private val networkController: NetworkControllerInterface) {

    private val TAG = NetworkHandler::class.java.simpleName

    fun handleCommunicationResult(result: Result<String, FuelError>, response: Response) {
        val (data, error) = result
        if (wasHostNameResponse(response)) {
            uiListener.onUpdateIpsList(createOutcomeMap(data, response))
        }
        if (error != null) {
            logDebug(data, response, error)
            if (wasHostNameResponse(response)) {
                Log.d(TAG, "This ip: $response.url.host is not running an IOT server on port $PORT")
            }
            uiListener.onFailure(networkController)
        } else {
            if (data.equals(POSITIVE_RESPONSE)) {
                uiListener.onSuccess()
            }
            if (data.toString().startsWith(STATE_ACTIVATED_RESPONSE)) {
                uiListener.onAlreadyActivated()
            }
            if (wasHostNameResponse(response)) {
                Log.d(TAG, "Found existing host: " + data)
            }
        }
    }

    private fun createOutcomeMap(data: String?, response: Response): HashMap<String, String> {
        val outcome = HashMap<String, String>(2)
        if (data != null) {
            outcome.put(HOSTNAME_KEY, data)
        } else {
            outcome.put(HOSTNAME_KEY, "")
        }
        outcome.put(IP_KEY, response.url.host)
        return outcome
    }

    private fun wasHostNameResponse(response: Response) =
            response.url.path == HOSTNAME_COMMAND

    private fun logDebug(data: String?, response: Response, error: FuelError?) {
        Log.d(TAG, "data: " + data)
        Log.d(TAG, "response: " + response.toString())
        Log.d(TAG, "error: " + error)
    }
}