package org.team5940.krpc

import krpc.client.Connection
import krpc.client.services.SpaceCenter
import org.ghrobotics.lib.mathematics.threedim.geometry.Translation3d
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.Radian
import org.ghrobotics.lib.mathematics.units.derived.degrees
import org.ghrobotics.lib.mathematics.units.derived.inDegrees
import org.team5940.krpc.util.PitchYawRollHelper
import kotlin.properties.Delegates

@Suppress("MemberVisibilityCanBePrivate")
open class ActiveCraftBase {

    val connection: Connection = Connection.newInstance()
    val spaceCenterInstance: SpaceCenter = SpaceCenter.newInstance(connection)
    val wrappedVessel: SpaceCenter.Vessel = spaceCenterInstance.activeVessel

    val wrappedControl: SpaceCenter.Control = wrappedVessel.control
    private val autoPilot = wrappedVessel.autoPilot

    val srfFlight: SpaceCenter.Flight = wrappedVessel.flight(wrappedVessel.orbit.body.referenceFrame)

    val twr: Double get() = (wrappedVessel.maxThrust / wrappedVessel.mass / 9.80655)

    val maxThrust = wrappedVessel.maxThrust.toDouble()

    val mass = wrappedVessel.mass.toDouble() // kg

    val globalLatLng: Pair<Double, Double>
        get() = (srfFlight.latitude to srfFlight.longitude)

    val surfaceAltitude get() = srfFlight.surfaceAltitude

    val verticalVelcoity get() = srfFlight.verticalSpeed

//    /**
//     * @return the location relative to the homeLocation
//     */
//    val globalPose: Pose2d
//        get() = Pose2d(globalLatLng, srfFlight.heading.degree)

    fun setGear(wantsDeployed: Boolean) {
        wrappedControl.gear = wantsDeployed
    }

    var rcs
        get() = wrappedControl.rcs
        set(newValue) {
            wrappedControl.rcs = newValue
        }

    var throttle
        get() = wrappedControl.throttle.toDouble()
        set(value) {
            wrappedControl.throttle = value.toFloat()
        }

    val whackyVelocityReferenceFrame = wrappedVessel.flight(SpaceCenter.ReferenceFrame.createHybrid(
            connection,
            wrappedVessel.orbit.body.referenceFrame,
            wrappedVessel.surfaceReferenceFrame,
            wrappedVessel.orbit.body.referenceFrame,
            wrappedVessel.orbit.body.referenceFrame))

    val pitchYawRoll get() = PitchYawRollHelper.getPitchYawRoll(connection, spaceCenterInstance, wrappedVessel)

    val pitch get() = whackyVelocityReferenceFrame.pitch.toDouble().degrees
    val roll get() = whackyVelocityReferenceFrame.roll.toDouble().degrees
    val heading get() = whackyVelocityReferenceFrame.heading.toDouble().degrees

    /**
     * Get or set the autopilot pitch, heading, roll
     */
    val direction: Translation3d
        get() = Translation3d(pitch.inDegrees(), heading.inDegrees(), roll.inDegrees())

    fun setSrfTargetDirection(pitch: SIUnit<Radian>, heading: SIUnit<Radian>, roll: SIUnit<Radian>) {
        autoPilot.targetRoll = roll.inDegrees().toFloat()
        autoPilot.targetPitchAndHeading(pitch.inDegrees().toFloat(), heading.inDegrees().toFloat())
    }

    var autoPilotEngaged by Delegates.observable(false) { _, _, newValue ->
            if (newValue) wrappedControl.sas = false
            if (newValue) autoPilot.engage() else autoPilot.disengage()
    }

    fun setManualPitchYawRoll(pitchInput: Double, yawInput: Double, rollInput: Double) {
        autoPilotEngaged = false
        wrappedControl.pitch = pitchInput.toFloat()
        wrappedControl.yaw = yawInput.toFloat()
        wrappedControl.roll = rollInput.toFloat()
    }

    protected operator fun Translation3d.minus(other: Translation3d) = Translation3d(x - other.x, y - other.y, z - other.z)



}
