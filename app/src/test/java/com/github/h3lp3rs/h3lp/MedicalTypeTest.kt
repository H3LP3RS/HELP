package com.github.h3lp3rs.h3lp

import com.github.h3lp3rs.h3lp.dataclasses.MedicalType
import com.github.h3lp3rs.h3lp.forum.ForumCategory
import org.junit.Assert
import org.junit.Test

class MedicalTypTest {

    @Test
    fun hasCategoryWorks() {
        val medicalType = MedicalType(listOf( ForumCategory.CARDIOLOGY))
        assert(medicalType.hasCategory(ForumCategory.CARDIOLOGY))
        assert(!medicalType.hasCategory(ForumCategory.DEFAULT_CATEGORY))
    }

}

