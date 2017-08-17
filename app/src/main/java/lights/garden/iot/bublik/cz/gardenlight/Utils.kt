package lights.garden.iot.bublik.cz.gardenlight

object Utils {
    fun createUrlFromIp(ipAddress: String) = "http://$ipAddress:${Const.PORT}"

    fun createHostnameRequestUrlFromGivenIp(ipAddress: String) =
            createUrlFromIp(ipAddress)+ Const.HOSTNAME_COMMAND
}