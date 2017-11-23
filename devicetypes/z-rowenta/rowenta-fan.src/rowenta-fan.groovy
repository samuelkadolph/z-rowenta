/**
 *  Rowenta Fan
 *
 *  Copyright 2017 Samuel Kadolph
 */

metadata {
  definition (name: "Rowenta Fan", namespace: "z-rowenta", author: "samuelkadolph") {
    capability "Switch"

    attribute "fanSpeed", "enum", ["low", "medium", "high", "boost"]

    command "low"
    command "medium"
    command "high"
    command "boost"
  }

  simulator {
  }

  tiles(scale: 2) {
    multiAttributeTile(name: "switch", type:"generic", width: 6, height: 4, canChangeIcon: true) {
      tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
        attributeState "off", label:"OFF", action:"switch.on", icon:"st.Appliances.appliances11", backgroundColor:"#FFFFFF", nextState:"turningOn"
        attributeState "on", label:"ON", action:"switch.off", icon:"st.Appliances.appliances11", backgroundColor:"#00A0DC", nextState:"turningOff"
        attributeState "turningOn", label:"TURNINGON", icon:"st.Appliances.appliances11", backgroundColor:"#00A0DC"
        attributeState "turningOff", label:"TURNINGOFF", icon:"st.Appliances.appliances11", backgroundColor:"#FFFFFF"
      }
    }

    standardTile("low", "device.fanSpeed", width: 2, height: 2, decoration: "flat") {
      state "not-low", label:"LOW", icon:"https://cdn.rawgit.com/samuelkadolph/z-rowenta/7e9ab608/icons/z-rowenta.low.png", backgroundColor:"#FFFFFF", action:"low", nextState:"changing"
      state "changing", label:"LOW", icon:"https://cdn.rawgit.com/samuelkadolph/z-rowenta/7e9ab608/icons/z-rowenta.low.png", backgroundColor:"#99E4FF"
      state "low", label:"LOW", icon:"https://cdn.rawgit.com/samuelkadolph/z-rowenta/7e9ab608/icons/z-rowenta.low.png", backgroundColor:"#00A0DC"
    }
    standardTile("medium", "device.fanSpeed", width: 2, height: 2, decoration: "flat") {
      state "not-medium", label:"MEDIUM", icon:"https://cdn.rawgit.com/samuelkadolph/z-rowenta/7e9ab608/icons/z-rowenta.medium.png", backgroundColor:"#FFFFFF", action:"medium", nextState:"changing"
      state "changing", label:"MEDIUM", icon:"https://cdn.rawgit.com/samuelkadolph/z-rowenta/7e9ab608/icons/z-rowenta.medium.png", backgroundColor:"#99E4FF"
      state "medium", label:"MEDIUM", icon:"https://cdn.rawgit.com/samuelkadolph/z-rowenta/7e9ab608/icons/z-rowenta.medium.png", backgroundColor:"#00A0DC"
    }
    standardTile("high", "device.fanSpeed", width: 2, height: 2, decoration: "flat") {
      state "not-high", label:"HIGH", icon:"https://cdn.rawgit.com/samuelkadolph/z-rowenta/7e9ab608/icons/z-rowenta.high.png", backgroundColor:"#FFFFFF", action:"high", nextState:"changing"
      state "changing", label:"HIGH", icon:"https://cdn.rawgit.com/samuelkadolph/z-rowenta/7e9ab608/icons/z-rowenta.high.png", backgroundColor:"#99E4FF"
      state "high", label:"HIGH", icon:"https://cdn.rawgit.com/samuelkadolph/z-rowenta/7e9ab608/icons/z-rowenta.high.png", backgroundColor:"#00A0DC"
    }

    standardTile("boost", "device.fanSpeed", width: 2, height: 2, decoration: "flat") {
      state "not-boost", label:"BOOST", icon:"https://cdn.rawgit.com/samuelkadolph/z-rowenta/7e9ab608/icons/z-rowenta.boost.png", backgroundColor:"#FFFFFF", action:"boost", nextState:"changing"
      state "changing", label:"BOOST", icon:"https://cdn.rawgit.com/samuelkadolph/z-rowenta/7e9ab608/icons/z-rowenta.boost.png", backgroundColor:"#99E4FF"
      state "boost", label:"BOOST", icon:"https://cdn.rawgit.com/samuelkadolph/z-rowenta/7e9ab608/icons/z-rowenta.boost.png", backgroundColor:"#00A0DC"
    }

    main "switch"
    details "switch", "low", "medium", "high", "boost"
  }
}

def boost() {
  return zwave.switchMultilevelV3.switchMultilevelSet(value: 4).format()
}

def high() {
  return zwave.switchMultilevelV3.switchMultilevelSet(value: 3).format()
}

def low() {
  return zwave.switchMultilevelV3.switchMultilevelSet(value: 1).format()
}

def medium() {
  return zwave.switchMultilevelV3.switchMultilevelSet(value: 2).format()
}

def off() {
  return zwave.basicV1.basicSet(value: 0x0).format()
}

def on() {
  return zwave.basicV1.basicSet(value: 0xFF).format()
}

def parse(String description) {
  def cmd = zwave.parse(description)

  if (cmd) {
    return zwaveEvent(cmd)
  } else {
    return null
  }
}

def speedToString(int value) {
  switch(value) {
    case 1: return "low"
    case 2: return "medium"
    case 3: return "high"
    case 4: return "boost"
    default: return null
  }
}

def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd) {
  def power = cmd.value == 0 ? "off" : "on"
  log.debug("powerValue is now ${cmd.value} (${power})")
  sendEvent(name: "switch", value: power)
}

def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv3.SwitchMultilevelReport cmd) {
  def speed = speedToString(cmd.value)
  log.debug("speedValue is now ${cmd.value} (${speed})")
  sendEvent(name: "fanSpeed", value: speed)
}
