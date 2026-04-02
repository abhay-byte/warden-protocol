package com.ivarna.wardenprotocol.data.model

data class NvidiaModelOption(
    val id: String,
    val label: String,
    val detail: String,
    val family: String,
    val recommended: Boolean = false
)

object NvidiaModelCatalog {
    const val DEFAULT_MODEL_ID = "meta/llama-4-maverick-17b-128e-instruct"

    val options: List<NvidiaModelOption> = listOf(
        NvidiaModelOption(
            id = DEFAULT_MODEL_ID,
            label = "Llama 4 Maverick 17B",
            detail = "Default model. Official NVIDIA NIM Llama 4 general-purpose multimodal instruct profile.",
            family = "Meta",
            recommended = true
        ),
        NvidiaModelOption(
            id = "meta/llama-4-scout-17b-16e-instruct",
            label = "Llama 4 Scout 17B",
            detail = "Official NVIDIA NIM Llama 4 scout profile for lighter and faster tests.",
            family = "Meta"
        ),
        NvidiaModelOption(
            id = "meta/llama-3.3-70b-instruct",
            label = "Llama 3.3 70B Instruct",
            detail = "Official NVIDIA NIM Meta profile for reasoning, math, and function calling.",
            family = "Meta"
        ),
        NvidiaModelOption(
            id = "nvidia/llama-3.3-nemotron-super-49b-v1.5",
            label = "Llama 3.3 Nemotron Super 49B",
            detail = "Official NVIDIA reasoning/chat profile with strong accuracy-efficiency tradeoff.",
            family = "NVIDIA"
        ),
        NvidiaModelOption(
            id = "Qwen/Qwen3-Next-80B-A3B-Instruct",
            label = "Qwen3-Next 80B Instruct",
            detail = "Official NVIDIA NIM Qwen 3 80B profile for agentic long-context instruction runs.",
            family = "Qwen"
        ),
        NvidiaModelOption(
            id = "mistral/mistral-small-24b-instruct-2501",
            label = "Mistral Small 24B",
            detail = "Official NVIDIA NIM Mistral Small 24B profile for lower-latency JSON-heavy tests.",
            family = "Mistral"
        ),
        NvidiaModelOption(
            id = "qwen/qwen2.5-coder-32b-instruct",
            label = "Qwen2.5 Coder 32B",
            detail = "Official NVIDIA NIM Qwen 2.5 profile that is valid and useful for structured output tests.",
            family = "Qwen"
        ),
        NvidiaModelOption(
            id = "mistralai/mistral-small-4-119b-2603",
            label = "Mistral Small 4 119B",
            detail = "Official NVIDIA NIM Mistral Small 4 profile for larger reasoning and coding comparisons.",
            family = "Mistral"
        ),
        NvidiaModelOption(
            id = "qwen/qwen2.5-coder-7b-instruct",
            label = "Qwen2.5 Coder 7B",
            detail = "Official NVIDIA NIM smaller Qwen 2.5 option for quick smoke tests.",
            family = "Qwen"
        )
    )

    fun resolve(modelId: String): NvidiaModelOption =
        options.firstOrNull { it.id == modelId }
            ?: NvidiaModelOption(
                id = modelId,
                label = modelId.substringAfterLast('/'),
                detail = "Custom model profile.",
                family = modelId.substringBefore('/', "Custom")
            )
}
