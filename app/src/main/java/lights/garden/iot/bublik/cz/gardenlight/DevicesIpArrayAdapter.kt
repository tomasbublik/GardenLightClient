package lights.garden.iot.bublik.cz.gardenlight

import android.content.Context
import android.widget.SimpleAdapter
import java.util.*

class DevicesIpArrayAdapter(context: Context, textViewResourceId: Int,
                            objects: List<Map<String, String>>) : SimpleAdapter(context, objects, textViewResourceId, arrayOf(Const.IP_KEY, Const.HOSTNAME_KEY), intArrayOf(android.R.id.text1, android.R.id.text2)) {

    private var mIdMap = HashMap<Map<String, String>, Int>()

    init {
        updateList(objects)
    }

    fun updateList(objects: List<Map<String, String>>) {
        synchronized(this) {
            mIdMap = HashMap()
            for (i in objects.indices) {
                mIdMap.put(objects[i], i)
            }
        }
    }

    override fun getItemId(position: Int): Long {
        val item = getItem(position)
        return mIdMap[item]!!.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }
}
