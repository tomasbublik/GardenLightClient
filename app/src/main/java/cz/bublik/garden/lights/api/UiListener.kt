package cz.bublik.garden.lights.api

interface UiListener {

    fun onUpdateIpsList(outcome: Map<String, String>)

    fun onCleanIpsList()

    fun onSuccess()

    fun onAlreadyActivated()

    fun onFailure(networkController: NetworkControllerInterface)

    fun setToggleButtonListener(networkController: NetworkController)
}