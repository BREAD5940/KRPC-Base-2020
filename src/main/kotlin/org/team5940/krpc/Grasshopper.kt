package org.team5940.krpc

import edu.wpi.first.wpilibj.controller.PIDController
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile
import krpc.client.services.Drawing
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.derived.degrees
import org.ghrobotics.lib.mathematics.units.derived.inRadians
import org.ghrobotics.lib.mathematics.units.inMilliseconds
import org.ghrobotics.lib.mathematics.units.seconds
import org.javatuples.Triplet
import org.team4069.keigen.*
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt


private val Triplet<Double, Double, Double>.norm: Float
    get() = sqrt(value0.pow(2) + value1.pow(2) + value2.pow(2)).toFloat()

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
        val constraints = TrapezoidProfile.Constraints(20.0, 7.0)
        val profile = TrapezoidProfile(
                constraints, TrapezoidProfile.State(50.0, 0.0),
                TrapezoidProfile.State(surfaceAltitude, verticalVelcoity)
        )

        autoPilotEngaged = false

        var lastState = profile.calculate(0.0)
        VerticalController.controller.reset()
        VerticalController.controller.enable()

        val start = missionElapsedTime
        var lastTime = start
        while (start + 60 > missionElapsedTime) {

            val setpoint = profile.calculate(missionElapsedTime - start)

            VerticalController.controller.update(vec(`2`).fill(surfaceAltitude, verticalVelcoity), vec(`2`).fill(setpoint.position, setpoint.velocity))

            val drag_ = vesselFlight.drag

            val ff = calcFF(currentSrfGravity, drag_.value1) // just the drag in axis of the engines
            val fb = VerticalController.controller.u[0, 0]

            val ffString = "ff ${ff.round()}"
            val fbString = " fb ${fb.round()}"

            Display.text.content = "alt ${surfaceAltitude.roundToInt()} ref ${setpoint.position.round()} \n${ffString + fbString}"

            println(setpoint.let { "pos ${it.position.round()} vel ${it.velocity.round()}" })

            throttle = ff + fb

            sleep(1.0.seconds / 30.0)
        }
        throttle = 0.0
    }

    private fun calcFb(setpoint: TrapezoidProfile.State): Double {
//        val ePos = setpoint.position - surfaceAltitude
//        val eVel = setpoint.velocity - verticalVelcoity
//        val error = vec(`2`).fill(ePos, eVel)
//        val accelSetpoint = VerticalController.k.times(error)
//        // f = m a
//        // thrust = m * a / f
//        return accelSetpoint[0, 0] * currentSrfGravity / maxThrust
        return VerticalController.controller.u[0, 0]
    }

    private fun calcFF(currentSrfGravity: Double, inAxisDrag: Double): Double {
        // f = ma
        val thrustToCounterDrag = (inAxisDrag / maxThrust)
        val thrustToCounterGravity = ((currentSrfGravity * mass) / maxThrust) / sin(pitch.inRadians())

        val ff = thrustToCounterDrag + thrustToCounterGravity //+ accelFF
        return ff
    }

    val drawing = Drawing.newInstance(connection)

    init {
//        canvas.rectTransform.size = org.javatuples.Pair(200.0, 100.0)
//        canvas.rectTransform.position = org.javatuples.Pair(100.0, 0.0)
//        ffEntry.rectTransform.position = org.javatuples.Pair(100.0, 100.0)
//        fbEntry.rectTransform.position = org.javatuples.Pair(100.0, 120.0)
    }

    fun sleep(time: SIUnit<Second>) = Thread.sleep(time.inMilliseconds().toLong())

}

private fun Double.round() = (this.let { if(isNaN() || isInfinite()) 0.0 else this } * 100.0).toInt() / 100.0

private fun Triplet<Double, Double, Double>.div(divisor: Float) = Triplet(value0 / divisor, value1 / divisor, value2 / divisor)