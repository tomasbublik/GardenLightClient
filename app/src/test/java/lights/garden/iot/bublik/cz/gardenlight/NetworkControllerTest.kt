package lights.garden.iot.bublik.cz.gardenlight

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Response
import com.nhaarman.mockito_kotlin.*
import cz.bublik.garden.lights.api.Const.HOSTNAME_COMMAND
import cz.bublik.garden.lights.api.Const.HOSTNAME_KEY
import cz.bublik.garden.lights.api.Const.IP_KEY
import cz.bublik.garden.lights.api.Const.POSITIVE_RESPONSE
import cz.bublik.garden.lights.api.Const.STATE_ACTIVATED_RESPONSE
import cz.bublik.garden.lights.api.Const.STATE_COMMAND
import cz.bublik.garden.lights.api.UiListener
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.net.URL
import java.util.*

class NetworkControllerTest {

    private val FAKE_IP = "192.168.1.1"
    private val FAKE_HOSTNAME = "raspberry"
    private val FAKE_URL = "http://$FAKE_IP"
    private val uiListenerMock: UiListener = mock()

    private lateinit var classUnderTest: FakeNetworkController

    @Before
    fun setup() {
        Fuel.testMode {
            timeout = 1
        }
    }

    private fun setupResponse(returnCode: String?, suffix: String) {
        val response = Response()
        if (returnCode != null) {
            response.httpResponseMessage = returnCode
        }
        response.httpStatusCode = 201
        response.url = URL(FAKE_URL + suffix)
        classUnderTest = FakeNetworkController(response, uiListenerMock)
    }

    @Test
    fun generatedDummyTest() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun shallCallAlreadyActivatedWhenZeroIsReturned() {
        setupResponse(STATE_ACTIVATED_RESPONSE, STATE_COMMAND)
        classUnderTest.checkState()

        verify(uiListenerMock).onAlreadyActivated()
    }

    @Test
    fun shallCallNothingWhenOneIsReturned() {
        setupResponse("1", STATE_COMMAND)
        classUnderTest.checkState()

        verify(uiListenerMock, never()).onAlreadyActivated()
    }

@Test
    fun shallCallSuccessWhenOKIsReturned() {
        setupResponse(POSITIVE_RESPONSE, STATE_COMMAND)
        classUnderTest.checkState()

        verify(uiListenerMock).onSuccess()
    }

    @Test
    fun shallUpdateIpsListWhenHostnameIsReturned() {
        setupResponse("raspberry", HOSTNAME_COMMAND)
        classUnderTest.getHostnameByIp(FAKE_IP)

        verify(uiListenerMock, never()).onAlreadyActivated()
        verify(uiListenerMock).onUpdateIpsList(any())
    }

    @Test
    fun shallUpdateIpsListEvenWhenHostnameIsNotReturned() {
        setupResponse(null, HOSTNAME_COMMAND)
        val outcomeMap = createExpectedOutcome("")

        classUnderTest.getHostnameByIp(FAKE_IP)

        verify(uiListenerMock).onUpdateIpsList(argThat { OutcomeMatcher(outcomeMap).matches(this) })
    }

    @Test
    fun IpsListShallContainHostnameWhenHostnameIsReturned() {
        setupResponse(FAKE_HOSTNAME, HOSTNAME_COMMAND)
        val outcomeMap = createExpectedOutcome(FAKE_HOSTNAME)

        classUnderTest.getHostnameByIp(FAKE_IP)

        verify(uiListenerMock).onUpdateIpsList(argThat { OutcomeMatcher(outcomeMap).matches(this) })
    }

    private fun createExpectedOutcome(hostname: String): HashMap<String, String> {
        val outcomeMap = HashMap<String, String>(2)
        outcomeMap.put(HOSTNAME_KEY, hostname)
        outcomeMap.put(IP_KEY, FAKE_IP)
        return outcomeMap
    }
}