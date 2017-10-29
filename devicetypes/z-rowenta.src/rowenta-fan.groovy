metadata {
  definition (name: "Rowenta Fan", namespace: "z-rowenta", author: "samuelkadolph") {
    capability "Switch Level"
  }

  simulator {
    status "on":  "switch:on"
    status "off": "switch:off"

    reply "on":"on"
    reply "off":"off"

    [5,10,25,33,50,66,75,99].each {
      status "$it%": "switch:on,level:$it"
    }
    reply "setLevel: 0":"switch:off,level:0"
    (1..99).each {
      reply "setLevel: $it":"switch:on,level:$it"
    }
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
  log.trace description
  def pairs = description.split(",")
  def result = []
  pairs.each {
    def pair = it.split(":")
    result << createEvent(name: pair[0].trim(), value: pair[1].trim())
  }
  log.trace result
  result
}

def on() {
  'on'
}

def off() {
  'off'
}

def setLevel(value) {
  "setLevel: $value"
}

def refresh() {
  'refresh'
}
