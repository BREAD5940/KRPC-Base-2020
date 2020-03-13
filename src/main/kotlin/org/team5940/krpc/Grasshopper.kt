package org.team5940.krpc

import edu.wpi.first.wpilibj.Timer
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.derived.degrees
import org.ghrobotics.lib.mathematics.units.derived.inDegrees
import org.ghrobotics.lib.mathematics.units.derived.radians
import org.ghrobotics.lib.mathematics.units.inMilliseconds
import org.ghrobotics.lib.mathematics.units.seconds

object Grasshopper : ActiveCraftBase() {

    fun start() {
        stateSpace()
    }

    fun exampleHop() {
        throttle = 1.0
        sleep(1.seconds)
        throttle = 0.0
    }

    fun exampleHopWithAutopilot() {
        setGear(true)
        throttle = 1.0
        rcs = true
        autoPilotEngaged = true
        setSrfTargetDirection(90.degrees, 0.degrees, 0.degrees)
        sleep(3.seconds)
        throttle = 0.0
    }

    fun stateSpace() {
        val start = Timer.getFPGATimestamp()
        while (Timer.getFPGATimestamp() < start + 30) {
            println(GrasshopperController.pitchHeadingToXY(pitch, heading).let { "x ${it.x.radians.inDegrees()} y ${it.y.radians.inDegrees()}" })
            sleep(0.2.seconds)
        }
    }


    fun sleep(time: SIUnit<Second>) = Thread.sleep(time.inMilliseconds().toLong())

}