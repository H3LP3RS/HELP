package com.github.h3lp3rs.h3lp

import com.github.h3lp3rs.h3lp.dataclasses.MedicalType
import com.github.h3lp3rs.h3lp.forum.ForumCategory
import com.github.h3lp3rs.h3lp.messaging.Message
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is
import org.junit.Assert
import org.junit.Test

class MessageTest {

    @Test
    fun noParameterConstructorWork() {
        val msg = Message()
        assertThat(msg.message, equalTo(""))
    }



}
