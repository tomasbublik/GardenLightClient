package cz.bublik.garden.lights.api

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result

interface NetworkControllerInterface {

    fun handleCommunicationResult(data:Map<String,Any?>): (Request, Response, Result<String, FuelError>) -> Unit

    fun setToggleButtonListener()

    fun checkState()

    fun getHostnameByIp(ipAddress: String)
}
