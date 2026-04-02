package com.ivarna.wardenprotocol.data.model

data class NvidiaModelOption(
    val id: String,
    val label: String,
    val detail: String,
    val family: String,
    val recommended: Boolean = false
)

object NvidiaModelCatalog {
    const val RECOMMENDED_MODEL_ID = "nvidia/llama-3.3-nemotron-super-49b-v1.5"

    val options: List<NvidiaModelOption> = listOf(
        NvidiaModelOption(
            id = RECOMMENDED_MODEL_ID,
            label = "Llama 3.3 Nemotron Super 49B",
            detail = "Recommended for structured reasoning, chat, and instruction following.",
            family = "NVIDIA",
            recommended = true
        ),
        NvidiaModelOption(
            id = "qwen/qwen3.5-122b-a10b",
            label = "Qwen 3.5 122B A10B",
            detail = "Current custom Qwen profile kept for direct comparison.",
            family = "Qwen"
        ),
        NvidiaModelOption(
            id = "meta/llama-3.3-70b-instruct",
            label = "Llama 3.3 70B Instruct",
            detail = "Strong reasoning, math, general knowledge, and function calling.",
            family = "Meta"
        ),
        NvidiaModelOption(
            id = "meta/llama-4-maverick-17b-128e-instruct",
            label = "Llama 4 Maverick 17B",
            detail = "General-purpose Llama 4 profile with broad instruction coverage.",
            family = "Meta"
        ),
        NvidiaModelOption(
            id = "meta/llama-4-scout-17b-16e-instruct",
            label = "Llama 4 Scout 17B",
            detail = "Lighter Llama 4 option for fast testing and multilingual runs.",
            family = "Meta"
        ),
        NvidiaModelOption(
            id = "Qwen/Qwen3-Next-80B-A3B-Instruct",
            label = "Qwen3-Next 80B Instruct",
            detail = "Agentic instruct model with strong long-context behavior.",
            family = "Qwen"
        ),
        NvidiaModelOption(
            id = "mistral/mistral-small-24b-instruct-2501",
            label = "Mistral Small 24B",
            detail = "Low-latency test option with solid reasoning and instruction following.",
            family = "Mistral"
        ),
        NvidiaModelOption(
            id = "qwen/qwen2.5-coder-32b-instruct",
            label = "Qwen2.5 Coder 32B",
            detail = "Code-heavy model that can still be useful for structured JSON tasks.",
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
