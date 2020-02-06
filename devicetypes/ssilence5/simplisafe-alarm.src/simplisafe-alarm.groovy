/**
 *  SimpliSafe integration for SmartThings
 *
 *  Copyright 2015 Felix Gorodishter
 *	Modifications by Toby Harris - 2/10/2018
 *  Modifications by Scott Silence - 2/6/2020
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *	  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */

String getVersionNum() 		{ return "0.0.1" }
String getVersionLabel() 	{ return "Simplisafe Alarm, version ${getVersionNum()}" }

metadata {	
	definition (name: "SimpliSafe Alarm", namespace: "ssilence5", author: "Scott Silence") {
		capability "Alarm"
		capability "Polling"
       // capability "Contact Sensor"
		capability "Carbon Monoxide Detector"
		//capability "Presence Sensor"
		capability "Smoke Detector"
        capability "Temperature Measurement"
        capability "Water Sensor"
        
		command "off"				//Redundant, defined as part of Alarm
		command "home",					[]
		command "away",					[]		
        //command "setOOOOOff",			[]
		//command "setHHHHHome",		[]
		//command "setAway",			[]
		command "update_state",			[]
        command "generateEvent",		[]
        
		attribute "events", "string"
		attribute "messages", "string"
		attribute "status", "string"
        attribute "state", "string"
	}

tiles(scale: 2) {
    multiAttributeTile(name:"status", type: "generic", width: 6, height: 4){
        tileAttribute ("device.status", key: "PRIMARY_CONTROL") {
            attributeState "off", label:'${name}', icon: "st.security.alarm.off", backgroundColor: "#505050"
            attributeState "home", label:'${name}', icon: "st.Home.home4", backgroundColor: "#00BEAC"
            attributeState "away", label:'${name}', icon: "st.security.alarm.on", backgroundColor: "#008CC1"
			attributeState "pending off", label:'${name}', icon: "st.security.alarm.off", backgroundColor: "#ffffff"
			attributeState "pending away", label:'${name}', icon: "st.Home.home4", backgroundColor: "#ffffff"
			attributeState "pending home", label:'${name}', icon: "st.security.alarm.on", backgroundColor: "#ffffff"
			attributeState "away_count", label:'countdown', icon: "st.security.alarm.on", backgroundColor: "#ffffff"
			attributeState "failed set", label:'error', icon: "st.secondary.refresh", backgroundColor: "#d44556"
			attributeState "alert", label:'${name}', icon: "st.alarm.beep.beep", backgroundColor: "#ffa81e"
			attributeState "alarm", label:'${name}', icon: "st.security.alarm.alarm", backgroundColor: "#d44556"
			attributeState "carbonMonoxide", label:'${name}', icon: "st.alarm.carbon-monoxide.carbon-monoxide", backgroundColor: "#d44556"
			attributeState "smoke", label:'${name}', icon: "st.alarm.smoke.smoke", backgroundColor: "#d44556"
			attributeState "temperature", label:'${name}', icon: "st.alarm.temperature.freeze", backgroundColor: "#d44556"
			attributeState "water", label:'${name}', icon: "st.alarm.water.wet", backgroundColor: "#d44556"
        }
		
		//tileAttribute("device.temperature", key: "SECONDARY_CONTROL", wordWrap: true) {
		//	attributeState("temperature", label:'${currentValue}', unit:"dF", defaultState: true)
		//}
    }	
	
    standardTile("off", "device.alarm", width: 2, height: 2, canChangeIcon: false, inactiveLabel: true, canChangeBackground: false) {
        state ("off", label:"off", action:"off", icon: "st.security.alarm.off", backgroundColor: "#008CC1", nextState: "pending")
        state ("away", label:"off", action:"off", icon: "st.security.alarm.off", backgroundColor: "#505050", nextState: "pending")
        state ("home", label:"off", action:"off", icon: "st.security.alarm.off", backgroundColor: "#505050", nextState: "pending")
        state ("pending", label:"pending", icon: "st.security.alarm.off", backgroundColor: "#ffffff")
	}
	
    standardTile("away", "device.alarm", width: 2, height: 2, canChangeIcon: false, inactiveLabel: true, canChangeBackground: false) {
        state ("off", label:"away", action:"away", icon: "st.security.alarm.on", backgroundColor: "#505050", nextState: "pending") 
		state ("away", label:"away", action:"away", icon: "st.security.alarm.on", backgroundColor: "#008CC1", nextState: "pending")
        state ("home", label:"away", action:"away", icon: "st.security.alarm.on", backgroundColor: "#505050", nextState: "pending")
		state ("pending", label:"pending", icon: "st.security.alarm.on", backgroundColor: "#ffffff")
		state ("away_count", label:"pending", icon: "st.security.alarm.on", backgroundColor: "#ffffff")
	}
	
    standardTile("home", "device.alarm", width: 2, height: 2, canChangeIcon: false, inactiveLabel: true, canChangeBackground: false) {
        state ("off", label:"home", action:"home", icon: "st.Home.home4", backgroundColor: "#505050", nextState: "pending")
        state ("away", label:"home", action:"home", icon: "st.Home.home4", backgroundColor: "#505050", nextState: "pending")
		state ("home", label:"home", action:"home", icon: "st.Home.home4", backgroundColor: "#008CC1", nextState: "pending")
		state ("pending", label:"pending", icon: "st.Home.home4", backgroundColor: "#ffffff")
	}
		valueTile("events", "device.events", width: 6, height: 2, canChangeIcon: false, inactiveLabel: true, canChangeBackground: false, decoration: "flat", wordWrap: true) {
			state ("default",  action: "poll", label:'${currentValue}')
		}

		main(["status"])
		details(["status","off", "away", "home", "events"])
	}
}

def installed() {
  	LOG("${device.label} being installed",2,null,'info')
	//if (device.label?.contains('TestingForInstall')) return	// we're just going to be deleted in a second...
	updated()
}

def uninstalled() {
	LOG("${device.label} being uninstalled",2,null,'info')
}

def updated() {
	LOG("${getVersionLabel()} updated",1,null,'trace')
    state.version = getVersionLabel()
}
 
//Local Function that alls global
def setState(String value) {
	parent.setState(this, value)
}

// handle commands
def off() {
	log.info "Setting SimpliSafe mode to 'Off'"
    setState("off")
}

def home() { 
	log.info "Setting SimpliSafe mode to 'Home'"
    setState("home")
}

def away() {
	log.info "Setting SimpliSafe mode to 'Away'"
    setState("away")
}

def generateEvent(Map results) {
	log.info ("generateEvents(): parsing data ${results}")
    Integer objectsUpdated = 0
    
    //Loop through the results
    if (results) {
    	results.each {name, value ->
        	objectsUpdated++
        	log.info ("generateEvent() - In each loop: object #${objectsUpdated} name: ${name} value: ${value}")
            
            String sendValue = value.toString()
            
            switch(name) {
            	case 'status':
                	sendEvent(name: 'status', value: sendValue)
                	break;
               	
                case 'alarm':
                	sendEvent(name: 'alarm', value: sendValue)
                	break;
                    
                case 'temp':
                	sendEvent(name: 'temperature', value: sendValue, unit: "dF")
                	break;
               	
                case 'message':
                	sendEvent(name: 'messages', value: sendValue)
                	break;
                    
                case 'carbonMonoxide':
                	sendEvent(name: 'carbonMonoxide', value: sendValue)
                	break;
                    
                case 'smoke':
                	sendEvent(name: 'smoke', value: sendValue)
                	break;
                    
                case 'water':
                	sendEvent(name: 'water', value: sendValue)
                	break;
                    
               	case 'event':
                	sendEvent(name: 'events', value: sendValue)
                    
                // Nothing for now
				default:
					break;
                    
            }
        }
    }
}

def poll() {
	log.info ("Device Initiated Poll event")
	parent.pollChild(this)
}

/* TODO : What too do with this?
def updateModeandPresence() {
	//Get the current alarm state
    def alarm_state = device.currentValue("alarm")
    
    //Set presence	
    log.info "Alarm State2: $alarm_state"
	def alarm_presence = ['OFF':'present', 'HOME':'present', 'AWAY':'not present']
	sendEvent(name: 'presence', value: alarm_presence.getAt(alarm_state))
    
    log.info "Updating Parent"
    parent.updateState("Test")
    
    //Set Location Mode
    switch(alarm_state.toString()) {
    	case "OFF":
        	if (location.modes?.find{it.name == "Off"}) {
                location.setMode("Home")
            }  else {
                log.warn "Tried to change to undefined mode '${newMode}'"
            }
           	log.info "Set Location Mode to: Off"
            break
        default:
        	log.info "No Location Mode"
            break
    }
}*/

boolean debugLevel(level=3) {
	Integer dbg = device.currentValue('debugLevel')?.toInteger()
	Integer debugLvlNum = (dbg ?: (getParentSetting('debugLevel') ?: level)) as Integer
	return ( debugLvlNum >= level?.toInteger() )
}

void LOG(message, level=3, child=null, logType="debug", event=false, displayEvent=false) {
	def prefix = debugLevel(5) ? 'LOG: ' : ''
	if (logType == null) logType = 'debug'

	if (debugLevel(level)) {
		log."${logType}" "${prefix}${message}"
		//if (event) { debugEvent(message, displayEvent) }
	}
}

void debugEvent(message, displayEvent = false) {
	def results = [
		name: "appdebug",
		descriptionText: message,
		displayed: displayEvent,
		isStateChange: true
	]
	if ( debugLevel(4) ) { log.debug "Generating AppDebug Event: ${results}" }
	sendEvent (results)
}

def getParentSetting(String settingName) {
	// def ST = (state?.isST != null) ? state?.isST : isST
	//return isST ? parent?.settings?."${settingName}" : parent?."${settingName}"
    return parent?.settings?."${settingName}"
}