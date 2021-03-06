package lights.garden.iot.bublik.cz.gardenlight

import cz.bublik.garden.lights.api.Const.HOSTNAME_KEY
import cz.bublik.garden.lights.api.Const.IP_KEY
import org.mockito.ArgumentMatcher
import java.util.*

class OutcomeMatcher(private val expected: HashMap<String, String>) : ArgumentMatcher<Map<String, String>> {

    override fun matches(argument: Map<String, String>?): Boolean {
        if (argument !is HashMap<String, String>) {
            return false
        }
        return argument[HOSTNAME_KEY] == expected[HOSTNAME_KEY] && argument[IP_KEY] == expected[IP_KEY]
    }

    override fun toString(): String {
        return "[Matches]"
    }
}
