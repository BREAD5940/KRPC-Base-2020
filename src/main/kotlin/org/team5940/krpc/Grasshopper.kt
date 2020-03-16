package org.team5940.krpc

import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.derived.degrees
import org.ghrobotics.lib.mathematics.units.inMilliseconds
import org.ghrobotics.lib.mathematics.units.seconds
import org.javatuples.Triplet
import kotlin.math.pow
import kotlin.math.sqrt


private val Triplet<Double, Double, Double>.norm: Float
    get() = sqrt(value0.pow(2) + value1.pow(2) + value2.pow(2)).toFloat()

object Grasshopper : ActiveCraftBase() {

    fun start() {
        exampleHop()
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

    fun sleep(time: SIUnit<Second>) = Thread.sleep(time.inMilliseconds().toLong())

}

private fun Double.round() = (this.let { if (isNaN() || isInfinite()) 0.0 else this } * 100.0).toInt() / 100.0

private fun Triplet<Double, Double, Double>.div(divisor: Float) = Triplet(value0 / divisor, value1 / divisor, value2 / divisor)