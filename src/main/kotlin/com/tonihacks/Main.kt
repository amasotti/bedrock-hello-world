package com.tonihacks

import aws.sdk.kotlin.runtime.auth.credentials.ProfileCredentialsProvider
import aws.sdk.kotlin.services.bedrock.BedrockClient
import aws.sdk.kotlin.services.bedrock.model.FoundationModelSummary
import aws.sdk.kotlin.services.bedrock.model.ListFoundationModelsRequest
import aws.sdk.kotlin.services.bedrockruntime.BedrockRuntimeClient
import aws.sdk.kotlin.services.bedrockruntime.model.InvokeModelRequest
import aws.sdk.kotlin.services.bedrockruntime.model.InvokeModelResponse

suspend fun main() {
    listModels()
    helloBedrock()
}

suspend fun listModels(): List<FoundationModelSummary>? {
    BedrockClient {
            region = "eu-central-1"
            credentialsProvider = ProfileCredentialsProvider(profileName = "private")
        }
        .use { bedrockClient ->
            val response = bedrockClient.listFoundationModels(ListFoundationModelsRequest {})
            response.modelSummaries?.forEach { model ->
                println("==========================================")
                println(" Model ID: ${model.modelId}")
                println("------------------------------------------")
                println(" Name: ${model.modelName}")
                println(" Provider: ${model.providerName}")
                println(" Input modalities: ${model.inputModalities}")
                println(" Output modalities: ${model.outputModalities}")
                println(" Supported customizations: ${model.customizationsSupported}")
                println(" Supported inference types: ${model.inferenceTypesSupported}")
                println("------------------------------------------\n")
            }
            return response.modelSummaries
        }
}

suspend fun helloBedrock() {
    BedrockRuntimeClient {
            region = "eu-central-1"
            credentialsProvider = ProfileCredentialsProvider(profileName = "private")
        }
        .use { client ->
            val prompt = "What is the capital of Germany?"

            // https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-titan-text.html
            val request: InvokeModelRequest = InvokeModelRequest {
                modelId = "amazon.titan-text-lite-v1"
                contentType = "application/json"
                accept = "application/json"
                body =
                    """
{
    "inputText": "$prompt",
    "textGenerationConfig": {
        "maxTokenCount": 4096,
        "stopSequences": [],
        "temperature": 0,
        "topP": 1
    }
}
"""
                        .trimIndent()
                        .toByteArray()
            }

            val invokeModelResponse: InvokeModelResponse = client.invokeModel(request)

            println("Response: ${invokeModelResponse.body.toString(Charsets.UTF_8)}")

            client.close()
        }
}
