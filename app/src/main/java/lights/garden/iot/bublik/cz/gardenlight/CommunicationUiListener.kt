package lights.garden.iot.bublik.cz.gardenlight

import android.widget.ImageView
import android.widget.ToggleButton
import java.util.*

class CommunicationUiListener(private val mainActivity: MainActivity) : UiListener {

    private var ipArrayAdapter: DevicesIpArrayAdapter

    private val toggleButton: ToggleButton
    private val imageView: ImageView

    private var ipViewList: ArrayList<Map<String, String>> = ArrayList()

    init {
        ipArrayAdapter = DevicesIpArrayAdapter(mainActivity, android.R.layout.simple_list_item_2, ipViewList)
        toggleButton = mainActivity.findViewById(R.id.toggleButtonId) as ToggleButton
        imageView = mainActivity.findViewById(R.id.lightsImage) as ImageView
    }

    override fun onCleanIpsList() {
        object : Thread() {
            override fun run() {
                ipViewList.clear()
                updateIpsList()
            }
        }.start()
    }

    override fun onUpdateIpsList(outcome: Map<String, String>) {
        ipViewList.add(outcome)
        updateIpsList()
    }

    private fun updateIpsList() {
        ipArrayAdapter.updateList(ipViewList)
        mainActivity.runOnUiThread {
            ipArrayAdapter.notifyDataSetChanged()
        }
    }

    override fun onSuccess() {
        if (toggleButton.isChecked) {
            imageView.setImageResource(R.drawable.selected_image)
        } else {
            imageView.setImageResource(R.drawable.unselected_image)
        }
    }

    override fun onAlreadyActivated() {
        imageView.setImageResource(R.drawable.selected_image)
        toggleButton.isChecked = true
    }

    override fun onFailure(networkController: NetworkControllerInterface) {
        toggleButton.setOnCheckedChangeListener(null)
        toggleButton.isChecked = false
        imageView.setImageResource(R.drawable.unselected_image)
        networkController.setToggleButtonListener()
    }

    override fun setToggleButtonListener(networkController: NetworkController) {
        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                networkController.turnLightsOn()
            } else {
                networkController.turnLightsOff()
            }
        }
    }

    override fun getIpArrayAdapter(): DevicesIpArrayAdapter {
        return ipArrayAdapter
    }
}
