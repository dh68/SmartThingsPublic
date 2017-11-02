/**
 *  My Remote Sensor
 *  v1.1.3
 *
 *  Copyright 2015 Yves Racine
 *  LinkedIn profile: ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/
 *
 *  Developer retains all right, title, copyright, and interest, including all copyright, patent rights, trade secret 
 *  in the Background technology. May be subject to consulting fees under the Agreement between the Developer and the Customer. 
 *  Developer grants a non exclusive perpetual license to use the Background technology in the Software developed for and delivered 
 *  to Customer under this Agreement. However, the Customer shall make no commercial use of the Background technology without
 *  Developer's written consent.
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
  *  Software Distribution is restricted and shall be done only with Developer's written approval.
 *
 *  For installation, please refer to readme file under
 *     https://github.com/yracine/device-type.myecobee/blob/master/smartapps/readme.ecobee3RemoteSensor
 *
 */
metadata {
	// Automatically generated. Make future change here.
	definition (name: "My Remote Sensor", namespace: "yracine", author: "Yves Racine") {
		capability "Temperature Measurement"
//		capability "Relative Humidity Measurement"
		capability "Sensor"	
		capability "Motion Sensor"
	}

	// simulator metadata
	simulator {
		for (int i = 0; i <= 100; i += 10) {
			status "${i}F": "temperature: $i F"
		}

		for (int i = 0; i <= 100; i += 10) {
			status "${i}%": "humidity: ${i}%"
		}
	}

	// UI tile definitions
	tiles(scale: 2) {
		multiAttributeTile(name:"remoteMulti", type: "generic", width: 2, height: 2, canChangeIcon: true){
			tileAttribute("device.temperatureDisplay", key: "PRIMARY_CONTROL") {
				attributeState("default", label:'${currentValue}°', unit:"dF", backgroundColor:"#269bd2") 
			}
			tileAttribute("device.motion", key: "SECONDARY_CONTROL") {
				attributeState("default", label:'${currentValue}')			
			}	
		}
		standardTile("motion", "device.motion", inactiveLabel: false,, width:6, height:4, canChangeIcon: false) {
			state "active", label:'motion', icon:"st.motion.motion.active", backgroundColor:"#53a7c0"
			state "inactive", label:'no motion', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff"
		}	
		valueTile("temperature", "device.temperature", width: 6, height: 4,canChangeIcon: true) {
			state("temperature", label:'${currentValue}', unit:"F",
				backgroundColors:[
					// Celsius Color Range
					[value: 0, color: "#153591"],
					[value: 7, color: "#1e9cbb"],
					[value: 15, color: "#90d2a7"],
					[value: 23, color: "#44b621"],
					[value: 29, color: "#f1d801"],
					[value: 33, color: "#d04e00"],
					[value: 36, color: "#bc2323"],
					// Fahrenheit Color Range
					[value: 40, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 92, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				]
			)
		}
//		valueTile("humidity", "device.humidity", inactiveLabel: false) {
//			state "humidity", label:'${currentValue}% humidity', unit:""
//		}
        
	main(["temperature"])
		details(["temperature", "motion"])
//		details(["temperature", "motion", "humidity"])
	}
}

// Parse incoming device messages to generate events
def parse(String description) {
	def name = parseName(description)
	def value = parseValue(description)
	def unit = name == "temperature" ? getTemperatureScale() : (name == "humidity" ? "%" : null)
	def result = createEvent(name: name, value: value, unit: unit)
	log.debug "Parse returned ${result?.descriptionText}"
	return result
}

private String parseName(String description) {
	if (description?.startsWith("temperature: ")) {
		return "temperature"
	} else if (description?.startsWith("humidity: ")) {
		return "humidity"
	}
	null
}

private String parseValue(String description) {
	if (description?.startsWith("temperature: ")) {
		return zigbee.parseHATemperatureValue(description, "temperature: ", getTemperatureScale())
	} else if (description?.startsWith("humidity: ")) {
		def pct = (description - "humidity: " - "%").trim()
		if (pct.isNumber()) {
			return Math.round(new BigDecimal(pct)).toString()
		}
	}
	null
}