/**
 *  Rowenta Fan
 *
 *  Copyright 2017 Samuel Kadolph
 */
metadata {
  definition (name: "Rowenta Fan", namespace: "z-rowenta", author: "samuelkadolph") {
    capability "Switch Level"
  }

  simulator {
  }

  tiles {
    standardTile("switch", "device.switch", width: 2, height: 2) {
      state "off", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
      state "on", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#00A0DC", nextState:"turningOff"
      state "turningOn", label:'${name}', icon:"st.switches.switch.on", backgroundColor:"#00A0DC"
      state "turningOff", label:'${name}', icon:"st.switches.switch.off", backgroundColor:"#ffffff"
    }
    controlTile("levelSliderControl", "device.level", "slider", height: 2, width: 1, inactiveLabel: false) {
      state "level", action:"setLevel"
    }
    standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
      state "default", label:"", action:"refresh", icon:"st.secondary.refresh"
    }

    main "switch"
    details "switch", "levelSliderControl", "refresh"
  }
}

def parse(String description) {
    def result = null
    def cmd = zwave.parse(description)

    if (cmd) {
        result = zwaveEvent(cmd)
        log.debug("Parsed ${cmd} to ${result.inspect()}")
    } else {
        log.debug("Non-parsed event: ${description}")
    }

    return result
}

def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd) {
  log.debug("Report!")
}

def setLevel() {
  log.debug("Executing 'setLevel'")
}