package lights.garden.iot.bublik.cz.gardenlight

interface UiListener {

    fun onUpdateIpsList(outcome: Map<String, String>)

    fun onCleanIpsList()

    fun getIpArrayAdapter(): DevicesIpArrayAdapter?

    fun onSuccess()

    fun onAlreadyActivated()

    fun onFailure(networkController: NetworkControllerInterface)

    fun setToggleButtonListener(networkController: NetworkController)
}