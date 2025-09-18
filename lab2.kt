package com.example.homework.lab2

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class SmartDevice(val name: String, val category: String) {

    var deviceStatus: String = "offline"
        protected set

    open val deviceType = "unknown"

    open fun turnOn() {
        deviceStatus = "on"
        println("[INFO] $name is now ON")
    }

    open fun turnOff() {
        deviceStatus = "off"
        println("[INFO] $name is now OFF")
    }

    open fun printDeviceInfo() {
        println("Device: $name | Category: $category | Type: $deviceType | Status: $deviceStatus")
    }
}

class SmartTvDevice(deviceName: String, deviceCategory: String) :
    SmartDevice(name = deviceName, category = deviceCategory) {

    override val deviceType = "Smart TV"

    private var speakerVolume by RangeRegulator(initialValue = 2, minValue = 0, maxValue = 100)
    private var channelNumber by RangeRegulator(initialValue = 1, minValue = 0, maxValue = 200)

    fun increaseSpeakerVolume() {
        if (deviceStatus == "on") {
            speakerVolume++
            println("[TV] Volume: $speakerVolume")
        } else println("[WARN] TV is off. Cannot increase volume.")
    }

    fun decreaseSpeakerVolume() {
        if (deviceStatus == "on") {
            speakerVolume--
            println("[TV] Volume: $speakerVolume")
        } else println("[WARN] TV is off. Cannot decrease volume.")
    }

    fun nextChannel() {
        if (deviceStatus == "on") {
            channelNumber++
            println("[TV] Channel: $channelNumber")
        } else println("[WARN] TV is off. Cannot change channel.")
    }

    fun previousChannel() {
        if (deviceStatus == "on") {
            channelNumber--
            println("[TV] Channel: $channelNumber")
        } else println("[WARN] TV is off. Cannot change channel.")
    }

    override fun turnOn() {
        super.turnOn()
        println("$name booted with volume $speakerVolume and channel $channelNumber.")
    }
}

class SmartLightDevice(deviceName: String, deviceCategory: String) :
    SmartDevice(name = deviceName, category = deviceCategory) {

    override val deviceType = "Smart Light"

    private var brightnessLevel by RangeRegulator(initialValue = 0, minValue = 0, maxValue = 100)

    fun increaseBrightness() {
        if (deviceStatus == "on") {
            brightnessLevel++
            println("[Light] Brightness: $brightnessLevel")
        } else println("[WARN] Light is off. Cannot increase brightness.")
    }

    fun decreaseBrightness() {
        if (deviceStatus == "on") {
            brightnessLevel--
            println("[Light] Brightness: $brightnessLevel")
        } else println("[WARN] Light is off. Cannot decrease brightness.")
    }

    override fun turnOn() {
        super.turnOn()
        brightnessLevel = 2
        println("$name lights up at brightness $brightnessLevel.")
    }

    override fun turnOff() {
        super.turnOff()
        brightnessLevel = 0
    }
}

class SmartHome(
    private val smartTvDevice: SmartTvDevice,
    private val smartLightDevice: SmartLightDevice
) {
    var deviceTurnOnCount = 0
        private set

    fun turnOnTv() {
        if (smartTvDevice.deviceStatus != "on") {
            deviceTurnOnCount++
            smartTvDevice.turnOn()
        }
    }

    fun turnOffTv() {
        if (smartTvDevice.deviceStatus == "on") {
            deviceTurnOnCount--
            smartTvDevice.turnOff()
        }
    }

    fun increaseTvVolume() = smartTvDevice.increaseSpeakerVolume()
    fun decreaseTvVolume() = smartTvDevice.decreaseSpeakerVolume()
    fun changeTvChannelToNext() = smartTvDevice.nextChannel()
    fun changeTvChannelToPrevious() = smartTvDevice.previousChannel()
    fun printSmartTvInfo() = smartTvDevice.printDeviceInfo()

    fun turnOnLight() {
        if (smartLightDevice.deviceStatus != "on") {
            deviceTurnOnCount++
            smartLightDevice.turnOn()
        }
    }

    fun turnOffLight() {
        if (smartLightDevice.deviceStatus == "on") {
            deviceTurnOnCount--
            smartLightDevice.turnOff()
        }
    }

    fun increaseLightBrightness() = smartLightDevice.increaseBrightness()
    fun decreaseLightBrightness() = smartLightDevice.decreaseBrightness()
    fun printSmartLightInfo() = smartLightDevice.printDeviceInfo()

    fun turnOffAllDevices() {
        turnOffTv()
        turnOffLight()
    }
}

class RangeRegulator(
    initialValue: Int,
    private val minValue: Int,
    private val maxValue: Int
) : ReadWriteProperty<Any?, Int> {

    private var fieldData = initialValue

    override fun getValue(thisRef: Any?, property: KProperty<*>): Int = fieldData

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        if (value in minValue..maxValue) {
            fieldData = value
        } else {
            println("[WARN] ${property.name} out of range ($minValue–$maxValue)")
        }
    }
}

fun main() {
    val tv = SmartTvDevice("Android TV", "Entertainment")
    val light = SmartLightDevice("Google Light", "Utility")
    val smartHome = SmartHome(tv, light)

    smartHome.turnOnTv()
    smartHome.increaseTvVolume()
    smartHome.changeTvChannelToNext()
    smartHome.printSmartTvInfo()

    smartHome.turnOnLight()
    smartHome.increaseLightBrightness()
    smartHome.printSmartLightInfo()

    println("Devices currently on: ${smartHome.deviceTurnOnCount}")
    smartHome.turnOffAllDevices()
    println("Devices currently on after turning all off: ${smartHome.deviceTurnOnCount}")
}
