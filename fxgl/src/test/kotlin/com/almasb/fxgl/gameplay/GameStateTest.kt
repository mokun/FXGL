/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay

import javafx.beans.property.SimpleObjectProperty
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.isA
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsMapContaining
import org.hamcrest.collection.IsMapContaining.hasEntry
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class GameStateTest {

    private lateinit var gameState: GameState

    @BeforeEach
    fun setUp() {
        gameState = GameState()
    }

    @Test
    fun `Test put get`() {
        gameState.put("testBoolean", true)
        gameState.put("testInt", 5)
        gameState.put("testDouble", 10.5)
        gameState.put("testString", "StringData")
        gameState.put("testObject", Dummy("ObjectData"))

        assertTrue(gameState.getBoolean("testBoolean"))
        assertThat(gameState.getInt("testInt"), `is`(5))
        assertThat(gameState.getDouble("testDouble"), `is`(10.5))
        assertThat(gameState.getString("testString"), `is`("StringData"))
        assertTrue(gameState.getType("testObject") == (SimpleObjectProperty::class.java))
        assertThat(gameState.getObject<Dummy>("testObject").data, `is`("ObjectData"))

        assertThat(gameState.getBoolean("testBoolean"), `is`(gameState.booleanProperty("testBoolean").value))
        assertThat(gameState.getInt("testInt"), `is`(gameState.intProperty("testInt").value))
        assertThat(gameState.getDouble("testDouble"), `is`(gameState.doubleProperty("testDouble").value))
        assertThat(gameState.getString("testString"), `is`(gameState.stringProperty("testString").value))
        assertThat(gameState.getObject<Dummy>("testObject"), `is`(gameState.objectProperty<Dummy>("testObject").value))
    }

    @Test
    fun `Get all properties`() {
        gameState.put("testInt", 1)
        gameState.put("testString", "data")

        val map = gameState.getProperties()

        assertThat(map.size, `is`(2))
        assertThat(map, hasEntry("testInt", "1"))
        assertThat(map, hasEntry("testString", "data"))
    }

    @Test
    fun `Test set`() {
        gameState.put("testInt", 100)

        gameState.setValue("testInt", 200)
        assertThat(gameState.getInt("testInt"), `is`(200))
    }

    @Test
    fun `Do not allow duplicates`() {
        assertThrows(IllegalArgumentException::class.java, {
            gameState.put("testInt", 1)
            gameState.put("testInt", 2)
        })
    }

    @Test
    fun `Throw if property name not found`() {
        assertThrows(IllegalArgumentException::class.java, {
            gameState.getBoolean("notFound")
        })
    }

    @Test
    fun `Test increment`() {
        gameState.put("testInt", 1)
        gameState.put("testDouble", 1.0)

        gameState.increment("testInt", +9)
        assertThat(gameState.getInt("testInt"), `is`(10))

        gameState.increment("testInt", -10)
        assertThat(gameState.getInt("testInt"), `is`(0))

        gameState.increment("testDouble", +1.0)
        assertThat(gameState.getDouble("testDouble"), `is`(2.0))

        gameState.increment("testDouble", -3.0)
        assertThat(gameState.getDouble("testDouble"), `is`(-1.0))
    }

    @Test
    fun `Test listeners`() {
        gameState.put("testInt", 10)

        var count = 0

        gameState.addListenerKt<Int>("testInt", { prev, now ->
            assertThat(prev, `is`(10))
            count += now
        })

        gameState.setValue("testInt", 25)
        assertThat(count, `is`(25))
    }

    @Test
    fun `Game difficulty`() {
        gameState.gameDifficultyProperty().value = GameDifficulty.NIGHTMARE

        assertThat(gameState.getGameDifficulty(), `is`(GameDifficulty.NIGHTMARE))
    }

    private class Dummy(var data: String) {}
}