package com.github.h3lp3rs.h3lp.parsers

import com.github.h3lp3rs.h3lp.model.parsers.GDurationJSONParser
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// Example of a time value returned by the directions api, it corresponds to 1 day, 3 hours and 2 mins
// number of seconds
const val NB_SECONDS = 97320

// The corresponding number of seconds in text
const val NB_SECONDS_TEXT = "1 day 3 hours 2 mins"

class GDurationJSONParserTest {
    @Test
    fun parseResultReturnsNullWithWrongInputs() {
        val parser = GDurationJSONParser

        val emptyJson = JSONObject("{}")
        assertNull(parser.parseResult(emptyJson))

        val wrongJson = JSONObject("{results: 0.0}")
        assertNull(parser.parseResult(wrongJson))
    }

    @Test
    fun parseResultIsCorrectForRealInputs() {
        // Example of a JSON the directions API could return
        val json = "{\"geocoded_waypoints\":[{\"geocoder_status\":\"OK\"" +
                ",\"place_id\":\"ChIJ6SYZ0s4xjEcR6XVj4C-rYmY\",\"types\":[]},{\"geocoder_status\":\"OK\"," +
                "\"place_id\":\"ChIJ6SYZ0s4xjEcR6XVj4C-rYmY\",\"types\":[]}],\"routes\":[{\"bounds\":{\"northeast\":" +
                "{\"lat\":46.5254611,\"lng\":6.6032943},\"southwest\":{\"lat\":46.5254416,\"lng\":6.603284700000001}}," +
                "\"copyrights\":\"Map data ©2022\",\"legs\":[{\"duration\":{\"value\":$NB_SECONDS}," +
                "\"summary\":\"Chem. de la Gravière\",\"warnings\":[],\"waypoint_order\":[]}]}],\"status\":\"OK\"}"

        val obj = JSONObject(json)
        assertEquals(NB_SECONDS_TEXT, GDurationJSONParser.parseResult(obj))
    }
}
