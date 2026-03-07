package com.pricetracker.app.data

/**
 * Represents the price direction of a stock symbol.
 */
enum class PriceDirection {
    UP, DOWN, NEUTRAL
}

/**
 * Immutable data class representing the current state of a single stock symbol.
 */
data class StockSymbol(
    val symbol: String,
    val currentPrice: Double,
    val previousPrice: Double,
    val direction: PriceDirection = PriceDirection.NEUTRAL,
    val description: String = ""
) {
    val priceChange: Double get() = currentPrice - previousPrice
    val priceChangePercent: Double
        get() = if (previousPrice != 0.0) (priceChange / previousPrice) * 100.0 else 0.0
}

/**
 * Predefined stock metadata: symbol -> description.
 */
val STOCK_METADATA: Map<String, String> = mapOf(
    "AAPL"  to "Apple Inc. — designs, manufactures, and markets smartphones, personal computers, tablets, wearables, and accessories.",
    "GOOG"  to "Alphabet Inc. — multinational conglomerate specializing in internet-related services and products, including search, cloud, and advertising.",
    "TSLA"  to "Tesla, Inc. — electric vehicle and clean energy company, known for EVs, energy storage, and solar products.",
    "AMZN"  to "Amazon.com, Inc. — multinational technology company focusing on e-commerce, cloud computing, digital streaming, and AI.",
    "MSFT"  to "Microsoft Corporation — develops, licenses, and supports software, services, devices, and solutions worldwide.",
    "NVDA"  to "NVIDIA Corporation — designs graphics processing units (GPUs) for gaming, professional visualization, data centers, and automotive.",
    "META"  to "Meta Platforms, Inc. — social technology company building products to connect people, including Facebook, Instagram, and WhatsApp.",
    "NFLX"  to "Netflix, Inc. — subscription streaming service and production company offering movies, TV series, and documentaries worldwide.",
    "AMD"   to "Advanced Micro Devices — designs and markets CPUs, GPUs, FPGAs, and other semiconductor products.",
    "INTC"  to "Intel Corporation — designs and manufactures CPUs, chipsets, and computing-platform components.",
    "ORCL"  to "Oracle Corporation — provides database software, cloud-engineered systems, and enterprise software products.",
    "CRM"   to "Salesforce, Inc. — cloud-based software company specializing in CRM services and enterprise applications.",
    "ADBE"  to "Adobe Inc. — multimedia and creativity software provider, offering products like Photoshop, Acrobat, and Creative Cloud.",
    "PYPL"  to "PayPal Holdings, Inc. — online payment system used in most countries that supports online money transfers.",
    "UBER"  to "Uber Technologies, Inc. — technology company offering ride-hailing, food delivery, and freight transportation services.",
    "LYFT"  to "Lyft, Inc. — ride-sharing company offering transportation network services across North America.",
    "SPOT"  to "Spotify Technology S.A. — audio streaming and media services provider with a music, podcast, and audiobook catalog.",
    "SNAP"  to "Snap Inc. — camera and social media company most known for the Snapchat messaging app.",
    "TWTR"  to "Twitter / X Corp. — social media platform enabling users to post short messages and interact in real time.",
    "SQ"    to "Block, Inc. (formerly Square) — technology conglomerate with a focus on financial services and mobile payments.",
    "SHOP"  to "Shopify Inc. — cloud-based commerce platform that supports online stores and retail point-of-sale systems.",
    "ZM"    to "Zoom Video Communications, Inc. — video teleconferencing software program.",
    "PLTR"  to "Palantir Technologies Inc. — specializes in big data analytics software for government and commercial clients.",
    "COIN"  to "Coinbase Global, Inc. — cryptocurrency exchange platform facilitating the buying and selling of digital currencies.",
    "RIVN"  to "Rivian Automotive, Inc. — electric vehicle automaker focused on adventure and commercial delivery vehicles."
)

/**
 * Initial stock list with randomized starting prices.
 */
fun buildInitialStocks(): List<StockSymbol> =
    STOCK_METADATA.map { (symbol, description) ->
        StockSymbol(
            symbol = symbol,
            currentPrice = (50..500).random().toDouble() + (0..99).random() / 100.0,
            previousPrice = 0.0,
            direction = PriceDirection.NEUTRAL,
            description = description
        )
    }
