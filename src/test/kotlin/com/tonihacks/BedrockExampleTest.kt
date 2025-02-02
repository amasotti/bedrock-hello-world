package com.tonihacks

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class BedrockExampleTest {
    private val bedrock =
        BedrockExample(
            awsRegion = "eu-central-1",
            profile = "default",
            bedrockModelID = "amazon.titan-text-lite-v1",
        )

    @Test
    fun `should list available models`() = runBlocking {
        val models = bedrock.listModels()
        assertFalse(models!!.isEmpty(), "Should return available models")
    }

    @Test
    fun `should generate text from prompt`() = runBlocking {
        val response = bedrock.generateText("What is the capital of Germany?")
        assertNotNull(response, "Should return generated text")
    }
}
