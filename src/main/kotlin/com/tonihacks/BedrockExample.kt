package com.tonihacks

import aws.sdk.kotlin.runtime.auth.credentials.ProfileCredentialsProvider
import aws.sdk.kotlin.services.bedrock.BedrockClient
import aws.sdk.kotlin.services.bedrock.model.FoundationModelSummary
import aws.sdk.kotlin.services.bedrock.model.ListFoundationModelsRequest
import aws.sdk.kotlin.services.bedrockruntime.BedrockRuntimeClient
import aws.sdk.kotlin.services.bedrockruntime.model.InvokeModelRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class BedrockExample(
    private val awsRegion: String,
    private val profile: String,
    private val bedrockModelID: String,
) {
    private val logger = KotlinLogging.logger {}

    suspend fun listModels(): List<FoundationModelSummary>? =
        BedrockClient {
                region = awsRegion
                credentialsProvider = ProfileCredentialsProvider(profileName = profile)
            }
            .use { client ->
                val resp = client.listFoundationModels(ListFoundationModelsRequest {})

                resp.modelSummaries?.forEach { model ->
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

                return resp.modelSummaries
            }

    suspend fun generateText(prompt: String) =
        BedrockRuntimeClient {
                region = awsRegion
                credentialsProvider = ProfileCredentialsProvider(profileName = profile)
            }
            .use { client ->
                val response =
                    client.invokeModel(
                        InvokeModelRequest {
                            modelId = bedrockModelID
                            contentType = "application/json"
                            accept = "application/json"
                            body =
                                """
                    {
                        "inputText": "${prompt.replace(Regex("\\s+"), " ").trim()}",
                        "textGenerationConfig": {
                            "maxTokenCount": 2000,
                            "stopSequences": [],
                            "temperature": 1,
                            "topP": 0.7
                        }
                    }"""
                                    .trimIndent()
                                    .toByteArray()
                        }
                    )

                Json { ignoreUnknownKeys = true }
                    .decodeFromString<BedrockResponse>(response.body.toString(Charsets.UTF_8))
                    .results
                    .firstOrNull()
                    ?.outputText
                    ?.also { text -> logger.info { "\n${text.trim()}" } }
            }
}

@Serializable private data class BedrockResponse(val results: List<Result>)

@Serializable private data class Result(val outputText: String)
