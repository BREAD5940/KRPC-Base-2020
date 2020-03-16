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

    val orbitFlight: SpaceCenter.Flight = wrappedVessel.flight(wrappedVessel.orbit.body.referenceFrame)
    val surfaceFlight: SpaceCenter.Flight = wrappedVessel.flight(wrappedVessel.surfaceReferenceFrame)
    val surfaceReferenceFrame: SpaceCenter.ReferenceFrame = wrappedVessel.surfaceReferenceFrame // X is up, Y is north, Z is east
    val vesselReferenceFrame: SpaceCenter.ReferenceFrame = wrappedVessel.referenceFrame
    val vesselFlight: SpaceCenter.Flight = wrappedVessel.flight(vesselReferenceFrame)

    val twr: Double get() = (wrappedVessel.maxThrust / wrappedVessel.mass / 9.80655)

    val maxThrust get() = wrappedVessel.availableThrust

    val mass get() = wrappedVessel.mass.toDouble() // kg

    val currentBody get() = wrappedVessel.orbit.body
    val currentSrfGravity get() = currentBody.surfaceGravity.toDouble()
//    val drag get() = surfaceFlight.drag
    val craftOrientedDrag get() = vesselFlight.drag
    val missionElapsedTime get() = wrappedVessel.met

    val globalLatLng: Pair<Double, Double>
        get() = (orbitFlight.latitude to orbitFlight.longitude)

    val surfaceAltitude get() = orbitFlight.surfaceAltitude

    val verticalVelcoity get() = orbitFlight.verticalSpeed

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

    val hybridSrfReferenceFrame = SpaceCenter.ReferenceFrame.createHybrid(
            connection,
            wrappedVessel.orbit.body.referenceFrame, // position
            wrappedVessel.surfaceReferenceFrame, // rotation
            wrappedVessel.orbit.body.referenceFrame, // velocity
            wrappedVessel.orbit.body.referenceFrame)
    val whackyVelocityFlight = wrappedVessel.flight(hybridSrfReferenceFrame) // angular velocity

    val pitchYawRoll get() = PitchYawRollHelper.getPitchYawRoll(connection, spaceCenterInstance, wrappedVessel)

    val pitch get() = whackyVelocityFlight.pitch.toDouble().degrees
    val roll get() = whackyVelocityFlight.roll.toDouble().degrees
    val heading get() = whackyVelocityFlight.heading.toDouble().degrees

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
