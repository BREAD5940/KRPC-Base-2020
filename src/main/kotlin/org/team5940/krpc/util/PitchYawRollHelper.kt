package org.team5940.krpc.util

import krpc.client.Connection
import krpc.client.RPCException
import krpc.client.services.SpaceCenter
import org.ghrobotics.lib.mathematics.threedim.geometry.Translation3d
import org.javatuples.Triplet
import java.io.IOException

object PitchYawRollHelper {
    fun crossProduct(
            u: Triplet<Double, Double, Double>, v: Triplet<Double, Double, Double>): Triplet<Double, Double, Double> {
        return Triplet(
                u.value1 * v.value2 - u.value2 * v.value1,
                u.value2 * v.value0 - u.value0 * v.value2,
                u.value0 * v.value1 - u.value1 * v.value0
        )
    }

    fun dotProduct(u: Triplet<Double, Double, Double>,
                   v: Triplet<Double, Double, Double>): Double {
        return u.value0 * v.value0 + u.value1 * v.value1 + u.value2 * v.value2
    }

    fun magnitude(v: Triplet<Double, Double, Double>): Double {
        return Math.sqrt(dotProduct(v, v))
    }

    // Compute the angle between vector x and y
    fun angleBetweenVectors(u: Triplet<Double, Double, Double>,
                            v: Triplet<Double, Double, Double>): Double {
        val dp = dotProduct(u, v)
        if (dp == 0.0) {
            return 0.0
        }
        val um = magnitude(u)
        val vm = magnitude(v)
        return Math.acos(dp / (um * vm)) * (180f / Math.PI)
    }

    @Throws(IOException::class, RPCException::class, InterruptedException::class)
    @JvmStatic
    fun getPitchYawRoll(connection: Connection, spaceCenter: SpaceCenter, vessel: SpaceCenter.Vessel): Translation3d {

        val vesselDirection = vessel.direction(vessel.surfaceReferenceFrame)

        // Get the direction of the vessel in the horizon plane
        val horizonDirection = Triplet(
                0.0, vesselDirection.value1, vesselDirection.value2)

        // Compute the pitch - the angle between the vessels direction
        // and the direction in the horizon plane
        var pitch = angleBetweenVectors(vesselDirection, horizonDirection)
        if (vesselDirection.value0 < 0) {
            pitch = -pitch
        }

        // Compute the heading - the angle between north
        // and the direction in the horizon plane
        val north = Triplet(0.0, 1.0, 0.0)
        var heading = angleBetweenVectors(north, horizonDirection)
        if (horizonDirection.value2 < 0) {
            heading = 360 - heading
        }

        // Compute the roll
        // Compute the plane running through the vessels direction
        // and the upwards direction
        val up = Triplet(1.0, 0.0, 0.0)
        val planeNormal = crossProduct(vesselDirection, up)
        // Compute the upwards direction of the vessel
        val vesselUp = spaceCenter.transformDirection(
                Triplet(0.0, 0.0, -1.0),
                vessel.referenceFrame, vessel.surfaceReferenceFrame)
        // Compute the angle between the upwards direction
        // of the vessel and the plane normal
        var roll = angleBetweenVectors(vesselUp, planeNormal)
        // Adjust so that the angle is between -180 and 180 and
        // rolling right is +ve and left is -ve
        when {
            vesselUp.value0 > 0 -> {
                roll *= -1.0
            }
            roll < 0 -> {
                roll += 180.0
            }
            else -> {
                roll -= 180.0
            }
        }

//        System.out.printf("pitch = %.1f, heading = %.1f, roll = %.1f\n",
//                pitch, heading, roll)

        return Translation3d(pitch, heading, roll)

    }
}