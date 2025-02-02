# AWS Bedrock Kotlin Example

A minimal proof of concept demonstrating how to use AWS Bedrock with the Kotlin SDK. The project shows two main functionalities:
1. Listing available foundation models
2. Generating text using the Titan model

## Example

Here's a sample prompt and response from the Titan model:

**PROMPT:**

```kotlin
val prompt = """
    Write a short, funny story about a time-traveling cat who
    accidentally ends up in ancient Egypt at the time of Pharaohs.
""".trimIndent()
```

**RESPONSE:**

_using max_tokens=2000 and temperature=1.0_

```text
Once upon a time, a mischievous cat named Missy found a time machine in her backyard. She was curious about its workings and decided to give it a try. 
As she stepped inside, she was surprised to find herself in the middle of an ancient Egyptian village. 
Missy quickly realized that she had traveled back in time to the time of the Pharaohs and pyramids.
Missy explored the village, marveling at the ancient structures and the way the people lived. 
She even managed to sneak into the pyramids and explore their secrets. 
But she soon realized that she had to get back to her own time before she was discovered.
[... rest of the story ...]
```

### Input Format

```json
{
  "inputText": "Your prompt here.",
  "textGenerationConfig": {
      "maxTokenCount": 2000,
      "stopSequences": [],
      "temperature": 1.0,
      "topP": 0.7
  }
}
```
You may want to check the doc page [Inference Request Parameters](https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters.html) on the AWS Bedrock documentation for more information on the parameters.
The structure of the input varies depending on the model you are using.

### Output Format

```json
/* Body only */
{
  "inputTextTokenCount":7,
  "results":[
    {"tokenCount":9,
      "outputText":"Generated answer.",
      "completionReason":"FINISH"
    }
  ]
}
```


## Usage

1. Clone the repository

```bash
  git clone  git@github.com:amasotti/bedrock-hello-world.git
```

2. Build the project

```bash
  ./gradlew build
```

3. Run the project

```bash
  ./gradlew run
```

## Tooling

- Kotlin v2.1.10
- Gradle v8.12


## Resources

- [Bedrock](https://docs.aws.amazon.com/bedrock/latest/userguide/what-is-bedrock.html)
  - [Examples](https://docs.aws.amazon.com/code-library/latest/ug/kotlin_1_bedrock_code_examples.html)
- [Kotlin SDK Docs](https://sdk.amazonaws.com/kotlin/api/latest/index.html)
  - [Kotlin SDK Main Repository](https://github.com/awslabs/aws-sdk-kotlin)
  - [Kotlin SDK Examples](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/creating-clients.html)