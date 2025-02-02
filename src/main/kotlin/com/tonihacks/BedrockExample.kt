package com.tonihacks

import aws.sdk.kotlin.runtime.auth.credentials.ProfileCredentialsProvider
import aws.sdk.kotlin.services.bedrock.BedrockClient
import aws.sdk.kotlin.services.bedrock.model.FoundationModelSummary
import aws.sdk.kotlin.services.bedrock.model.ListFoundationModelsRequest
import aws.sdk.kotlin.services.bedrockruntime.BedrockRuntimeClient
import aws.sdk.kotlin.services.bedrockruntime.model.InvokeModelRequest
import aws.sdk.kotlin.services.bedrockruntime.model.InvokeModelResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable data class BedrockResponse(val inputTextTokenCount: Int, val results: List<Result>)

@Serializable
data class Result(val tokenCount: Int, val outputText: String, val completionReason: String)

class BedrockExample(
    private val region: String,
    private val profile: String,
    private val modelId: String,
) {
    private val logger = KotlinLogging.logger {}

    suspend fun listModels(): List<FoundationModelSummary>? {
        BedrockClient {
                region = this@BedrockExample.region
                credentialsProvider = ProfileCredentialsProvider(profileName = profile)
            }
            .use { bedrockClient ->
                val response = bedrockClient.listFoundationModels(ListFoundationModelsRequest {})
                response.modelSummaries?.forEach { model ->
                    logger.info { "==========================================" }
                    logger.info { " Model ID: ${model.modelId}" }
                    logger.info { "------------------------------------------" }
                    logger.info { " Name: ${model.modelName}" }
                    logger.info { " Provider: ${model.providerName}" }
                    logger.info { " Input modalities: ${model.inputModalities}" }
                    logger.info { " Output modalities: ${model.outputModalities}" }
                    logger.info { " Supported customizations: ${model.customizationsSupported}" }
                    logger.info { " Supported inference types: ${model.inferenceTypesSupported}" }
                    logger.info { "------------------------------------------\n" }
                }
                return response.modelSummaries
            }
    }

    suspend fun helloBedrock(prompt: String) {
        BedrockRuntimeClient {
                region = this@BedrockExample.region
                credentialsProvider = ProfileCredentialsProvider(profileName = profile)
            }
            .use { client ->
                logger.debug { "Prompt: $prompt" }
                logger.debug { "Model: ${this@BedrockExample.modelId}" }

                val cleanPrompt = prompt.replace(Regex("\\s+"), " ").trim()

                val request: InvokeModelRequest = InvokeModelRequest {
                    modelId = "amazon.titan-text-lite-v1"
                    contentType = "application/json"
                    accept = "application/json"
                    body =
                        """
{
    "inputText": "$cleanPrompt",
    "textGenerationConfig": {
        "maxTokenCount": 2000,
        "stopSequences": [],
        "temperature": 1,
        "topP": 0.7
    }
}
"""
                            .trimIndent()
                            .toByteArray()
                }
                val invokeModelResponse: InvokeModelResponse = client.invokeModel(request)
                val responseBody = invokeModelResponse.body.toString(Charsets.UTF_8)
                logger.debug { "Raw response: $responseBody" }

                // Parse and log the response nicely
                val parsed =
                    Json { ignoreUnknownKeys = true }
                        .decodeFromString<BedrockResponse>(responseBody)

                logger.info { "\n=== Generated Text ===\n" }
                parsed.results.firstOrNull()?.let { result ->
                    result.outputText.split("\n\n").forEach { paragraph ->
                        if (paragraph.isNotBlank()) {
                            logger.info { paragraph.trim() + "\n" }
                        }
                    }
                }
                logger.info { "====================\n" }
            }
    }
}
