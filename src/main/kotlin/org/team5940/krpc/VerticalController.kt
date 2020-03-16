package org.team5940.krpc
import edu.wpi.first.wpilibj.controller.LinearQuadraticRegulator
import org.team4069.keigen.*

import edu.wpi.first.wpilibj.system.LinearSystem

object VerticalController {

    // states: [position, velocity]
    // inputs: [acceleration]
    // outputs: [position]
    // xdot = [ 0 1 | 0 0] x + [0 | 1] u
    val plant = LinearSystem(
            `2`, `1`, `1`,
            mat(`2`, `2`).fill(0.0, 1.0,
                    0.0, 0.0),
            vec(`2`).fill(0.0, 1.0),
            mat(`1`, `2`).fill(1.0, 0.0),
            vec(`1`).fill(0.0),
            vec(`1`).fill(-5.0),
            vec(`1`).fill(5.0)
    )

    val controller = LinearQuadraticRegulator(
            `2`, `1`, plant,
            vec(`2`).fill(0.05, 0.1),
            vec(`1`).fill(0.5),
            1.0 / 30.0
    )

    val k = controller.k

}