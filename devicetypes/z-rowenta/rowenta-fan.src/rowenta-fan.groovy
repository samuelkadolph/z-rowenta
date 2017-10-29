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
    standardTile("switch", "device.switch", width: 6, height: 2, canChangeIcon: true) {
      state "off", label:'${name}', action:"switch.on", icon:"st.Appliances.appliances11", backgroundColor:"#ffffff", nextState:"turningOn"
      state "on", label:'${name}', action:"switch.off", icon:"st.Appliances.appliances11", backgroundColor:"#00A0DC", nextState:"turningOff"
      state "turningOn", label:'${name}', icon:"st.Appliances.appliances11", backgroundColor:"#00A0DC"
      state "turningOff", label:'${name}', icon:"st.Appliances.appliances11", backgroundColor:"#ffffff"
    }

    standardTile("low", "device.switch", inactiveLabel: false, decoration: "flat") {
      state "default", label:"Low", action:"refresh"
    }
    standardTile("medium", "device.switch", inactiveLabel: false, decoration: "flat") {
      state "default", label:"Medium", action:"refresh"
    }
    standardTile("high", "device.switch", inactiveLabel: false, decoration: "flat") {
      state "default", label:"High", action:"refresh"
    }

    standardTile("boost", "device.switch", inactiveLabel: false, decoration: "flat") {
      state "default", label:"Boost", action:"refresh"
    }

    main "switch"
    details "switch", "low", "medium", "high", "boost"
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

def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv3.SwitchMultilevelReport cmd) {
  log.debug("level = ${cmd.value}")
}

def setLevel(value) {
  log.debug("Executing 'setLevel('${value}')")
}