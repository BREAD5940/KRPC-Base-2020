package org.team5940.krpc

import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.geometry.Translation2d
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.derived.degrees
import org.ghrobotics.lib.mathematics.units.derived.inRadians
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

        rcs = true
        wrappedControl.sas = false
        autoPilotEngaged = false
        setGear(true)
        GrasshopperController.reset()
        GrasshopperController.enable()

        val start = Timer.getFPGATimestamp()
        

        println("x, y, xHat, yHat, u_0, u_1")
        while (Timer.getFPGATimestamp() < start + 30.0) {
            // NORTH MUST BE UP
            val xy = GrasshopperController.pitchHeadingToXY(pitch, heading)

            println("${xy.x}, ${xy.y}, ${GrasshopperController.loop.getXHat(0)}, ${GrasshopperController.loop.getXHat(1)}, ${GrasshopperController.loop.getU(0)}, ${GrasshopperController.loop.getU(1)}")

            GrasshopperController.update(xy, Translation2d(0.0, 0.0))
            val u = GrasshopperController.loop.u
            setManualPitchYawRoll(
                    u[1, 0],
//                    0.0,
                    u[0, 0],

//                    0.0, 0.0,
                    0.0
//                    (0.0 - roll.inRadians()) * 2.0
            )

//            println(u.storage)

            sleep(1.0.seconds / 30.0)
        }
    }


    fun sleep(time: SIUnit<Second>) = Thread.sleep(time.inMilliseconds().toLong())

}