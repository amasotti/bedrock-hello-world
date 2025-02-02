package com.tonihacks

suspend fun main() {
    val bedrock =
        BedrockExample(
            awsRegion = "eu-central-1",
            profile = "private", // Only needed if you have multiple profiles
            bedrockModelID = "amazon.titan-text-lite-v1",
        )

    // Prompt for the model
    // bedrock.listModels()

    val prompt =
        """
        Write a short, funny story about a time-traveling cat who
        ends up in ancient Egypt at the time of Pharaohs and pyramids.
    """
            .trimIndent()

    bedrock.generateText(prompt)
}
