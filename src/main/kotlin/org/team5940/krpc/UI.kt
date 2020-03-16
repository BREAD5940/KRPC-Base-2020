package org.team5940.krpc

import krpc.client.Connection
import krpc.client.RPCException
import krpc.client.StreamException
import krpc.client.services.SpaceCenter
import krpc.client.services.UI
import krpc.client.services.UI.RectTransform
import org.javatuples.Triplet
import java.io.IOException


object Display {

    val connection: Connection = Connection.newInstance("User Interface Example")
    val spaceCenter = SpaceCenter.newInstance(connection)
    val ui: UI = UI.newInstance(connection)
    val canvas: UI.Canvas = ui.stockCanvas

    // Get the size of the game window in pixels
    val screenSize: org.javatuples.Pair<Double, Double> = canvas.rectTransform.size

    // Add a panel to contain the UI elements
    val panel: UI.Panel = canvas.addPanel(true)

    // Position the panel on the left of the screen
    val rect: RectTransform = panel.rectTransform


    // Add a button to set the throttle to maximum

    // Add some text displaying the total engine thrust
    val text: UI.Text = panel.addText("Thrust: 0 kN", true)


    init {
        text.rectTransform.position = org.javatuples.Pair(0.0, 0.0)
        text.color = Triplet(1.0, 1.0, 1.0)
        text.size = 13
        rect.size = org.javatuples.Pair(400.0, 100.0)
        rect.position = org.javatuples.Pair(110 - screenSize.value0 / 2, 0.0)
        text.alignment = UI.TextAnchor.UPPER_LEFT
    }

    // Set up a stream to monitor the throttle button
    val vessel = spaceCenter.activeVessel

//    @Throws(IOException::class, RPCException::class, InterruptedException::class, StreamException::class)
//    @JvmStatic
//    fun main(args: Array<String>) {
//
//            // Update the thrust text
//            text.content = String.format("Thrust: %.0f kN", vessel.thrust / 1000)
//            Thread.sleep(1000)
//        }
//    }
}