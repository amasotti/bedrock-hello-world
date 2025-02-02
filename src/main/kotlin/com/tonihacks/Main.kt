package com.tonihacks

import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

@Suppress("detekt.TooGenericExceptionCaught")
suspend fun main() {
    val bedrock =
        BedrockExample(
            region = "eu-central-1",
            profile = "private",
            modelId = "amazon.titan-text-lite-v1",
        )

    // Print a list of available models
    // bedrock.listModels()

    val prompt =
        """
    Write a short, funny story
    about a time-traveling cat who
    accidentally ends up in ancient Egypt at the time of Pharaohs.
"""
            .trimIndent()

    bedrock.helloBedrock(prompt)
}
