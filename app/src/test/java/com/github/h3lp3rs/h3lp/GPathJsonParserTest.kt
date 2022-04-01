package com.github.h3lp3rs.h3lp

import com.github.h3lp3rs.h3lp.util.GPathJSONParser
import com.google.android.gms.maps.model.LatLng
import junit.framework.Assert.assertEquals
import org.json.JSONObject
import org.junit.Test

class GPathJsonParserTest {
    @Test
    fun parseResultReturnsEmptyListWithWrongInputs() {
        val parser = GPathJSONParser
        val expected = listOf<LatLng>()

        val emptyJson = JSONObject("{}")
        assertEquals(expected, parser.parseResult(emptyJson))

        val wrongJson = JSONObject("{results: 0.0}")
        assertEquals(expected, parser.parseResult(wrongJson))
    }


    @Test
    fun parseResult_IsCorrectForRealInputs() {
        val json = "{\"geocoded_waypoints\":[{\"geocoder_status\":\"OK\"" +
                ",\"place_id\":\"ChIJ6SYZ0s4xjEcR6XVj4C-rYmY\",\"types\":[]},{\"geocoder_status\":\"OK\"," +
                "\"place_id\":\"ChIJ6SYZ0s4xjEcR6XVj4C-rYmY\",\"types\":[]}],\"routes\":[{\"bounds\":{\"northeast\":" +
                "{\"lat\":46.5254611,\"lng\":6.6032943},\"southwest\":{\"lat\":46.5254416,\"lng\":6.603284700000001}}," +
                "\"copyrights\":\"Map data ©2022\",\"legs\":[{\"distance\":{\"text\":\"2 m\",\"value\":2}," +
                "\"duration\":{},\"end_address\":\"Av. du Chablais 27, 1008 Prilly, Switzerland\"," +
                "\"end_location\":{\"lat\":46.5254611,\"lng\":6.6032943},\"start_address\":" +
                "\"Av. du Chablais 27, 1008 Prilly, Switzerland\",\"start_location\":{},\"steps\"" +
                ":[{\"distance\":{},\"duration\":{},\"end_location\":{\"lat\":46.5254611,\"lng\":6.6032943}," +
                "\"html_instructions\":\"Head\",\"polyline\":{\"points\":\"__~zGouhg@CA\"}" +
                ",\"start_location\":{\"lat\":46.5254416,\"lng\":6.603284700000001},\"travel_mode\":\"WALKING\"}]," +
                "\"traffic_speed_entry\":[],\"via_waypoint\":[]}],\"overview_polyline\":{\"points\":\"__~zGouhg@CA\"}," +
                "\"summary\":\"Chem. de la Gravière\",\"warnings\":[],\"waypoint_order\":[]}],\"status\":\"OK\"}"

        val points = listOf(
            LatLng(46.52544, 6.603280000000001),
            LatLng(46.52546, 6.60329),
        )
        val parser = GPathJSONParser
        val obj = JSONObject(json)
        assertEquals(points, parser.parseResult(obj))
    }


}