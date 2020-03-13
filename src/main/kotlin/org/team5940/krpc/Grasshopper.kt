package org.team5940.krpc

import edu.wpi.first.wpilibj.Timer
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.derived.degrees
import org.ghrobotics.lib.mathematics.units.inMilliseconds
import org.ghrobotics.lib.mathematics.units.seconds

object Grasshopper : ActiveCraftBase() {

    fun start() {
        exampleHopWithAutopilot()
    }

    fun exampleHop() {
        throttle = 1.0
        sleep(1.seconds)
        throttle = 0.0
    }

    fun exampleHopWithAutopilot() {
        setGear(true)
//        throttle = 1.0
//        rcs = true
//        autoPilotEngaged = true
//        setSrfTargetDirection(90.degrees, 0.degrees, 0.degrees)

        val start = Timer.getFPGATimestamp()
        while (Timer.getFPGATimestamp() < start + 5) {
            println("Pitch $pitch heading $heading roll $roll")
            println("alt $surfaceAltitude")
            sleep(0.2.seconds)
        }

        throttle = 0.0
    }


    fun sleep(time: SIUnit<Second>) = Thread.sleep(time.inMilliseconds().toLong())

}