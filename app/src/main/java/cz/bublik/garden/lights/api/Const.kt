package cz.bublik.garden.lights.api

object Const {
    val PORT: String = "3000"
    val HOSTNAME_TIMEOUT = 1000
    val RANGE_MULTIPLIER = 20
    val STATE_TIMEOUT = 2000
    val REQUEST_TIMEOUT = 3000
    val POSITIVE_RESPONSE = "OK"
    val STATE_ACTIVATED_RESPONSE = "0"

    val TURN_LIGHTS_ON_COMMAND: String = "/on"
    val TURN_LIGHTS_OFF_COMMAND: String = "/off"
    val STATE_COMMAND: String = "/state"
    val HOSTNAME_COMMAND: String = "/hostname"

    val PING_COMMAND = "ping -c 1 "
    val THREADS_NUMBER = 11

    val IP_KEY = "ip"
    val HOSTNAME_KEY = "hostname"
}