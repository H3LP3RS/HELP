package com.github.h3lp3rs.h3lp.parsers

import com.github.h3lp3rs.h3lp.model.parsers.GPlaceJSONParser
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Tests the parsing from json to google place
 */
class GPlaceJsonParserTest {

    @Test
    fun parseResultIsCorrectForRealInputs(){
        // Using real Google places API Http answers:
        val zeroResults = "{   \"html_attributions\" : [],   \"results\" : [],   \"status\" : \"ZERO_RESULTS\"}"
        val oneResult = "{   \"html_attributions\" : [],   \"results\" : [      {         \"business_status\" : \"OPERATIONAL\",         \"geometry\" : {            \"location\" : {               lat : 46.523694,               lng : 6.565280099999998            },            \"viewport\" : {               \"northeast\" : {                  \"lat\" : 46.5251174802915,                  \"lng\" : 6.566630630291501               },               \"southwest\" : {                  \"lat\" : 46.5224195197085,                  \"lng\" : 6.563932669708496               }            }         },         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/v1/png_71/hospital-71.png\",         \"icon_background_color\" : \"#F88181\",         \"icon_mask_base_uri\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/v2/hospital-H_pinlet\",         \"name\" : \"Ehc - Medical Center Arcades Epfl\",         \"opening_hours\" : {            \"open_now\" : true         },         \"photos\" : [            {               \"height\" : 2832,               \"html_attributions\" : [                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/115661628091789118002\\\"\\u003eEHC - Centre médical Arcades EPFL\\u003c/a\\u003e\"               ],               \"photo_reference\" : \"Aap_uEAN4FBYgA4sA0S_DJvgc95KF2JFKgIv9QqIrMQABpx_gITyMnUw5asZ602-ANcetbz286oZDKe4ViZDPN8hmGXgwZJeNv1oac3HcL2_FqfT-jFbzBh26lG3rOa2dvkkcADBOwJ78CbV3pomjC6OSx8CPnoy2zDH_IiRoJEQrhrZmj1p\",               \"width\" : 3790            }         ],         \"place_id\" : \"ChIJEQuI5QIxjEcRMDzHUu6hoVQ\",         \"plus_code\" : {            \"compound_code\" : \"GHF8+F4 Ecublens, Switzerland\",            \"global_code\" : \"8FR8GHF8+F4\"         },         \"rating\" : 3.5,         \"reference\" : \"ChIJEQuI5QIxjEcRMDzHUu6hoVQ\",         \"scope\" : \"GOOGLE\",         \"types\" : [ \"hospital\", \"health\", \"point_of_interest\", \"establishment\" ],         \"user_ratings_total\" : 40,         \"vicinity\" : \"Rue Louis Favre 6a, Ecublens\"      }   ],   \"status\" : \"OK\"}\n"

        val parser = GPlaceJSONParser

        var obj = JSONObject(zeroResults)
        assertEquals(arrayListOf<HashMap<String,String>>(), parser.parseResult(obj))

        obj = JSONObject(oneResult)
        val expectedPlace = HashMap<String, String>()
        expectedPlace["lat"] = 46.523694.toString()
        expectedPlace["lng"] = 6.565280099999998.toString()
        expectedPlace["name"] = "Ehc - Medical Center Arcades Epfl"

        assertEquals(arrayListOf(expectedPlace), parser.parseResult(obj))
    }

    @Test
    fun parseResultIsCorrectForMadeInputs() {
        val two_results = "{\"results\" : [{\"geometry\" : {" +
                "\"location\" : {" +
                "lat : 0.0," +
                "lng : 0.0" +
                "}}," +
                "\"name\" : \"NAME\"" +
                "}]}\n"

        val parser = GPlaceJSONParser
        val obj = JSONObject(two_results)
        val expectedPlace = HashMap<String, String>()
        expectedPlace["lat"] = 0.0.toString()
        expectedPlace["lng"] = 0.0.toString()
        expectedPlace["name"] = "NAME"

        assertEquals(arrayListOf(expectedPlace), parser.parseResult(obj))
    }

    @Test
    fun parseResultReturnsNullWithWrongInputs(){
        val parser = GPlaceJSONParser

        val emptyJson = JSONObject("{}")
        assertNull(parser.parseResult(emptyJson))

        val wrongJson = JSONObject("{results: 0.0}")
        assertNull(parser.parseResult(wrongJson))
    }
}