package com.multibank.pricetracker.data

val STOCK_METADATA: Map<String, String> = mapOf(
        "AAPL" to "Apple Inc. — global technology company known for iPhone, Mac computers, iPad tablets, and wearable devices.",
        "MSFT" to "Microsoft Corporation — builds operating systems, productivity software, and cloud computing services.",
        "GOOG" to "Alphabet Inc. — parent company of Google, focusing on search, online advertising, and cloud platforms.",
        "AMZN" to "Amazon — major e-commerce and cloud computing provider offering retail, logistics, and AWS services.",
        "TSLA" to "Tesla — manufacturer of electric vehicles and energy storage systems with a focus on sustainable transport.",
        "NVDA" to "NVIDIA — semiconductor company producing GPUs widely used in gaming, AI, and data centers.",
        "META" to "Meta Platforms — technology company operating Facebook, Instagram, and other social networking platforms.",
        "NFLX" to "Netflix — streaming entertainment platform producing and distributing movies and television content.",
        "AMD" to "Advanced Micro Devices — develops high-performance processors and graphics technologies.",
        "INTC" to "Intel — semiconductor manufacturer known for computer processors and integrated chip solutions.",
        "ORCL" to "Oracle — enterprise software provider specializing in database technologies and cloud infrastructure.",
        "CRM" to "Salesforce — provider of cloud-based customer relationship management and enterprise tools.",
        "ADBE" to "Adobe — creator of creative and document software such as Photoshop, Illustrator, and Acrobat.",
        "PYPL" to "PayPal — digital payments company enabling secure online money transfers worldwide.",
        "UBER" to "Uber — platform offering ride-hailing, delivery, and mobility services in many global cities.",
        "LYFT" to "Lyft — transportation network providing ride-sharing services primarily in North America.",
        "SPOT" to "Spotify — digital streaming platform offering music, podcasts, and audio content.",
        "SNAP" to "Snap — technology company behind the Snapchat multimedia messaging application.",
        "SQ" to "Block (formerly Square) — financial technology company providing payment and business tools.",
        "SHOP" to "Shopify — software platform that enables businesses to create and manage online stores.",
        "ZM" to "Zoom — communication software company known for its video conferencing solutions.",
        "PLTR" to "Palantir — develops large-scale data analytics platforms for organizations and governments.",
        "COIN" to "Coinbase — cryptocurrency trading platform allowing users to buy and sell digital assets.",
        "RIVN" to "Rivian — electric vehicle manufacturer producing trucks and adventure-focused EVs.",
        "ROKU" to "Roku — digital media company providing streaming devices and smart TV platforms."
    )
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
