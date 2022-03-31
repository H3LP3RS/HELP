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
        val r =
            "{\"geocoded_waypoints \":[{ \"geocoder_status \": \"OK\", \"place_id \": " +
            "\"ChIJ6SYZ0s4xjEcR6XVj4C-rYmY \", \"types \":[ \"street_address \"]},{ " +
            "\"geocoder_status \": \"OK \", \"place_id \": \"ChIJayGVss4xjEcRUGyvYL_noHc \", \"types" +
            " \":[ \"establishment \", \"health \", \"pharmacy \", \"point_of_interest \", \"store" +
            "\"]}], \"routes\":[{ \"bounds \":{ \"northeast \":{ \"lat \":46.5254416, \"lng \":" +
            "6.603284700000001}, \"southwest \":{ \"lat \":46.524352, \"lng \":6.602307}}, " +
            "\"copyrights \": \"Map data ©2022 \", \"legs\":[{ \"distance \":{ \"text \": \"0.2 km " +
            "\", \"value \":184}, \"duration \":{ \"text \": \"2 mins \", \"value \":149}, \"end_address \": \"Av. de Provence 84, 1007 Lausanne, Switzerland \", \"end_location \":{ \"lat \":46.524352, \"lng \":6.602307}, \"start_address \": \"Av. du Chablais 27, 1008 Prilly, Switzerland \", \"start_location \":{ \"lat \":46.5254416, \"lng \":6.603284700000001}, \"steps\":[{ \"distance \":{ \"text \": \"92 m \", \"value \":92}, \"duration \":{ \"text \": \"1 min \", \"value \":67}, \"end_location \":{ \"lat \":46.524613, \"lng \":6.60328}, \"html_instructions \": \"Head <b>south<b> on <b>Chem. de la Gravière<b> toward <b>Chem. de Malley<b> \", \"polyline\":{ \"points\":\"__~zGouhg@B@H?|@?D?p@?`@A\"},\"start_location\":{\"lat\":46.5254416,\"lng\":6.603284700000001},\"travel_mode\":\"WALKING\"},{\"distance\":{\"text\":\"67 m\",\"value\":67},\"duration\":{\"text\":\"1 min\",\"value\":61},\"end_location\":{\"lat\":46.524561,\"lng\":6.6024106},\"html_instructions\":\"\",\"maneuver\":\"turn-right\",\"polyline\":{\"points\":\"yy}zGouhg@L|BCn@\"},\"start_location\":{\"lat\":46.524613,\"lng\":6.60328},\"travel_mode\":\"WALKING\"},{\"distance\":{\"text\":\"25 m\",\"value\":25},\"duration\":{\"text\":\"1 min\",\"value\":21},\"end_location\":{\"lat\":46.524352,\"lng\":6.602307},\"html_instructions\":\"\",\"maneuver\":\"turn-left\",\"polyline\":{\"points\":\"oy}zGaphg@HB^N\"},\"start_location\":{\"lat\":46.524561,\"lng\":6.6024106},\"travel_mode\":\"WALKING\"}],\"traffic_speed_entry\":[],\"via_waypoint\":[]}],\"overview_polyline\":{\"points\":\"__~zGouhg@B@fA?v@?`@AL|BCn@HB^N\"},\"summary\":\"Chem. de la Gravière and Chem. de Malley\",\"warnings\":[\"Walking directions are in beta. Use caution – This route may be missing sidewalks or pedestrian paths.\"],\"waypoint_order\":[]}],\"status\":\"OK\"}"
        val points = listOf(
            LatLng(46.52544, 6.603280000000001),
            LatLng(46.525420000000004, 6.60327),
            LatLng(46.52537, 6.60327),
            LatLng(46.52506, 6.60327),
            LatLng(46.52503, 6.60327),
            LatLng(46.52478000000001, 6.60327),
            LatLng(46.52461, 6.603280000000001),
            LatLng(46.52461, 6.603280000000001),
            LatLng(46.52454, 6.602650000000001),
            LatLng(46.52456, 6.602410000000001),
            LatLng(46.52456, 6.602410000000001),
            LatLng(46.52451000000001, 6.602390000000001),
            LatLng(46.524350000000005, 6.60231)
        )
        val parser = GPathJSONParser

        val obj = JSONObject(r)
        assertEquals(points, parser.parseResult(obj))
    }


}