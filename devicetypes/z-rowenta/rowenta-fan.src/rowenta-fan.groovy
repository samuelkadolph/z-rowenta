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
    standardTile("switch", "device.switch", width: 6, height: 4, canChangeIcon: true) {
      state "off", label:"OFF", action:"switch.on", icon:"st.Appliances.appliances11", backgroundColor:"#FFFFFF", nextState:"turningOn"
      state "on", label:"ON", action:"switch.off", icon:"st.Appliances.appliances11", backgroundColor:"#00A0DC", nextState:"turningOff"
      state "turningOn", label:"TURNINGON", icon:"st.Appliances.appliances11", backgroundColor:"#00A0DC"
      state "turningOff", label:"TURNINGOFF", icon:"st.Appliances.appliances11", backgroundColor:"#FFFFFF"
    }

    standardTile("low", "device.fanSpeed", width: 2, height: 2, decoration: "flat") {
      state "not-low", label:"LOW", icon:"st.Home.home30", backgroundColor:"#FFFFFF", action:"low", nextState:"changing"
      state "changing", label:"LOW", icon:"st.Home.home30", backgroundColor:"#99E4FF"
      state "low", label:"LOW", icon:"st.Home.home30", backgroundColor:"#00A0DC"
    }
    standardTile("medium", "device.fanSpeed", width: 2, height: 2, decoration: "flat") {
      state "not-medium", label:"MEDIUM", backgroundColor:"#FFFFFF", action:"medium", nextState:"changing"
      state "changing", label:"MEDIUM", backgroundColor:"#99E4FF"
      state "medium", label:"MEDIUM", backgroundColor:"#00A0DC"
    }
    standardTile("high", "device.fanSpeed", width: 2, height: 2, decoration: "flat") {
      state "not-high", label:"HIGH", backgroundColor:"#FFFFFF", action:"high", nextState:"changing"
      state "changing", label:"HIGH", backgroundColor:"#99E4FF"
      state "high", label:"HIGH", backgroundColor:"#00A0DC"
    }

    valueTile("boost", "device.fanSpeed", width: 2, height: 2, decoration: "flat") {
      state "not-boost", label:"BOOST", backgroundColor:"#00A0DC", action:"boost", nextState:"changing"
      state "changing", label:"BOOST", backgroundColor:"#99E4FF"
      state "boost", label:"BOOST", backgroundColor:"#00A0DC"
    }

    main "switch"
    details "switch", "low", "medium", "high", "boost"
  }
}

def on() {
  return zwave.basicV1.basicSet(value: 0xFF).format()
}

def off() {
  return zwave.basicV1.basicSet(value: 0x0).format()
}

def low() {
  return zwave.switchMultilevelV3.switchMultilevelSet(value: 1).format()
}

def medium() {
  return zwave.switchMultilevelV3.switchMultilevelSet(value: 2).format()
}

def high() {
  return zwave.switchMultilevelV3.switchMultilevelSet(value: 3).format()
}

def boost() {
  return zwave.switchMultilevelV3.switchMultilevelSet(value: 4).format()
}

def parse(String description) {
  def cmd = zwave.parse(description)

  if (cmd) {
    return zwaveEvent(cmd)
  } else {
    return null
  }
}

def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd) {
  log.debug("SwitchBinaryReport value='${cmd.value}'")

  sendEvent(name: "switch", value: (cmd.value == 0 ? "off" : "on"));
}

def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv3.SwitchMultilevelReport cmd) {
  switch(cmd.value) {
    case 1: sendEvent(name: "fanSpeed", value: "low"); break;
    case 2: sendEvent(name: "fanSpeed", value: "medium"); break;
    case 3: sendEvent(name: "fanSpeed", value: "high"); break;
    case 4: sendEvent(name: "fanSpeed", value: "boost"); break;
  }

  log.debug("SwitchMultilevelReport value='${cmd.value}'")
}
