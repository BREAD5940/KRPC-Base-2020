package org.team5940.krpc

import edu.wpi.first.wpilibj.controller.ProfiledPIDController
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile
import krpc.client.services.Drawing
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.derived.degrees
import org.ghrobotics.lib.mathematics.units.inMilliseconds
import org.ghrobotics.lib.mathematics.units.seconds
import org.javatuples.Triplet
import kotlin.math.roundToInt


object Grasshopper : ActiveCraftBase() {

    fun start() {
        leap()
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

    fun leap() {
        // first boink
        val verticalController = ProfiledPIDController(1.0, 0.0, 0.0,
                TrapezoidProfile.Constraints(1.0, 2.0))

        verticalController.reset(surfaceAltitude)

        autoPilotEngaged = false

        val drawing = Drawing.newInstance(connection)

        val start = missionElapsedTime
        while (start + 60 > missionElapsedTime) {
            val out = verticalController.calculate(surfaceAltitude, 100.0)
            print("alt ${surfaceAltitude.roundToInt()} setpoint 100 command $out | ")
//            throttle = out
//            setSrfTargetDirection(90.degrees, 0.degrees, 0.degrees)

//            drawing.clear(false)
            drawing.addLine(Triplet(0.0, 0.0, 0.0), Triplet(1.0, 0.0, 0.0), surfaceReferenceFrame, true)

            println("drag ${drag.let { "x ${it.value0} y ${it.value1} z ${it.value2}" }}") // x is north, +y is south?
            sleep(1.0.seconds / 30.0)
        }
    }

    fun sleep(time: SIUnit<Second>) = Thread.sleep(time.inMilliseconds().toLong())

}