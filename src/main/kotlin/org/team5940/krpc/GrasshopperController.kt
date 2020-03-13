package org.team5940.krpc

import edu.wpi.first.wpilibj.controller.LinearQuadraticRegulator
import edu.wpi.first.wpilibj.estimator.KalmanFilter
import edu.wpi.first.wpilibj.geometry.Translation2d
import edu.wpi.first.wpilibj.system.LinearSystem
import edu.wpi.first.wpilibj.system.LinearSystemLoop
import edu.wpi.first.wpiutil.math.MatBuilder
import edu.wpi.first.wpiutil.math.Matrix
import edu.wpi.first.wpiutil.math.MatrixUtils
import edu.wpi.first.wpiutil.math.Nat
import edu.wpi.first.wpiutil.math.numbers.N1
import edu.wpi.first.wpiutil.math.numbers.N2
import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.*

/**
 * State space controller to control pitch and yaw of the craft
 * States: pitch, yaw, pitchRate, yawRate
 * Inputs: torque in pitch and yaw axis
 * Outputs: Pitch, yaw
 *
 * Angular acceleration is porportinal to torque, so we say that
 * pitchRateDot = MOI * pitchTorque
 *
 * xdot = [pitchRate, yawRate, pitchAlpha, yawAlpha]^T
 * xdot = [
 *
 *
 *              ]
 *
 */
object GrasshopperController {

    fun pitchHeadingToXY(pitch: SIUnit<Radian>, heading: SIUnit<Radian>): Translation2d {
        val translation = Translation2d(SIUnit((pitch - 90.degrees).value), (heading - 90.degrees).toRotation2d())
//        print("pitch ${pitch.inDegrees()} yaw ${heading.inDegrees()} ")
//        println(translation)
        return translation
    }

    val maxCommand = 1.0
    val torqueOverMoi = 0.221

    val plant = LinearSystem(
            Nat.N4(), Nat.N2(), Nat.N2(),
            MatBuilder(Nat.N4(), Nat.N4()).fill(
                    0.0, 0.0, 1.0, 0.0,
                    0.0, 0.0, 0.0, 1.0,
                    0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0
            ),
            MatBuilder(Nat.N4(), Nat.N2()).fill(
                    0.0, 0.0,
                    0.0, 0.0,
                    torqueOverMoi, 0.0,
                    0.0, torqueOverMoi
            ),
            MatBuilder(Nat.N2(), Nat.N4()).fill(
                    1.0, 0.0, 0.0, 0.0,
                    0.0, 1.0, 0.0, 0.0
            ),
            MatrixUtils.zeros(Nat.N2(), Nat.N2()),
            MatBuilder(Nat.N2(), Nat.N1()).fill(
                    -maxCommand, -maxCommand
            ),
            MatBuilder(Nat.N2(), Nat.N1()).fill(
                    maxCommand, maxCommand
            )
    )

    val controller = LinearQuadraticRegulator(
            Nat.N4(), Nat.N2(),
            plant,
            MatBuilder(Nat.N4(), Nat.N1()).fill(4.0.degrees.inRadians(), 4.0.degrees.inRadians(), 20.0.degrees.inRadians(), 20.0.degrees.inRadians()),
            MatBuilder(Nat.N2(), Nat.N1()).fill(-maxCommand, maxCommand),
            1.0 / 30.0
    )

    val filter = KalmanFilter(
            Nat.N4(), Nat.N2(), Nat.N2(),
            plant,
            MatBuilder(Nat.N4(), Nat.N1()).fill(2.degrees.inRadians(), 2.degrees.inRadians(), 4.degrees.inRadians(), 4.degrees.inRadians()),
            MatBuilder(Nat.N2(), Nat.N1()).fill(0.001.degrees.inRadians(), 0.001.degrees.inRadians()),
            1.0 / 30.0
    )

    /**
     * States: pitch, yaw, pitch rate, yaw rate.
     * Inputs: pitch and yaw commands, -1 to 1
     * Outputs: pitch and yaw
     */
    val loop = LinearSystemLoop(Nat.N4(), Nat.N2(), Nat.N2(),
            plant, controller, filter
    )

    fun enable() = loop.enable()
    fun reset() = loop.reset()
    fun disable() = loop.disable()

    fun update(currentOutput: Translation2d, reference: Translation2d): Matrix<N2, N1> {
        loop.correct(MatBuilder(Nat.N2(), Nat.N1()).fill(currentOutput.x, currentOutput.y))
        loop.nextR = MatBuilder(Nat.N4(), Nat.N1()).fill(reference.x, reference.y, 0.0, 0.0)
        loop.predict(1.0 / 30.0)
        return loop.u
    }

}