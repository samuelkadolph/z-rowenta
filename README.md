### z-rowenta

Using a [Z-Uno](http://z-uno.z-wave.me/) I've turned my boring pedestal fan (in my case a Rowenta VU5551) into a Z-Wave
compatible fan! The Z-Uno is effectively an Arduino with a Z-Wave chip. You even use the Arduino IDE to
[program it](https://z-uno.z-wave.me/getting-started/).

#### Hardware

This was the hardest part mostly because it requires an understanding of electrical engineering and reverse engineering
of existing ciruits.

#### Z-Uno Code

To compile and upload the code you'll need to complete the [Getting Started](https://z-uno.z-wave.me/getting-started/)
guide for the Z-Uno and then include it in your SmartThings Z-Wave network.

#### SmartThings Device Handler

Once you've included the Z-Uno into your network you can sign into the
[SmartThings website](https://graph.api.smartthings.com) and add this repo to your device handlers and pull the repo to
get the device handler. And after that you can edit the device to use this device handler. The code for device handler
is in [rowenta-fan.groovy](devicetypes/z-rowenta/rowenta-fan.src/rowenta-fan.groovy).
