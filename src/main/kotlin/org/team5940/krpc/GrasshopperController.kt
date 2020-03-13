package org.team5940.krpc

import edu.wpi.first.wpilibj.geometry.Translation2d
import org.ghrobotics.lib.mathematics.threedim.geometry.Translation3d
import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.Radian
import org.ghrobotics.lib.mathematics.units.derived.degrees
import org.ghrobotics.lib.mathematics.units.derived.toRotation2d

/**
 * State space controller to control pitch and yaw of the craft
 * States: pitch, yaw
 * Inputs: 2-axis gymbol
 */
object GrasshopperController {

    fun pitchHeadingToXY(pitch: SIUnit<Radian>, heading: SIUnit<Radian>): Translation2d {
        val translation = Translation2d(SIUnit((pitch - 90.degrees).value), heading.toRotation2d())
        println(translation)
        return translation
    }


}