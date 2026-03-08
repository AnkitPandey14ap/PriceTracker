# Feed flow and boundary violations (audit)

## Flow: UI → VM → use cases → repo → WebSocket

1. **FeedScreen** (`ui/feed/FeedScreen.kt`) — collects `FeedUiState` from `FeedViewModel`, uses `ConnectionState`, `StockSymbol`, `PriceDirection` in Composables.
2. **FeedViewModel** (`ui/feed/FeedViewModel.kt`) — uses `StartFeedUseCase`, `StopFeedUseCase`, `ObservePriceUpdatesUseCase`, `ObserveConnectionStateUseCase`; holds `List<StockSymbol>` and `PriceUpdate`; calls `buildInitialStocks()` from data.
3. **Use cases** — `StartFeedUseCase`, `StopFeedUseCase`, `ObservePriceUpdatesUseCase`, `ObserveConnectionStateUseCase` call `PriceFeedRepository` and expose its types.
4. **PriceFeedRepository** (interface in `domain/`) — declares `Flow<PriceUpdate>`, `StateFlow<ConnectionState>`, `start(List<StockSymbol>)`.
5. **PriceFeedRepositoryImpl** (`data/`) — implements repository using `WebSocketService`; exposes `ConnectionState` and `PriceUpdate` from data/ui.
6. **WebSocketService** (`data/`) — holds `StateFlow<ConnectionState>`, emits raw strings, uses `parsePrice`; runs simulation and reconnect logic.

## Boundary violations (file:line)

| Layer   | File | Line | Violation |
|---------|------|------|-----------|
| domain  | `domain/PriceFeedRepository.kt` | 3–5 | Imports `data.PriceUpdate`, `data.StockSymbol`, `ui.feed.ConnectionState` |
| domain  | `domain/ObservePriceUpdatesUseCase.kt` | 3 | Imports `data.PriceUpdate` |
| domain  | `domain/StartPriceFeedUseCase.kt` | 3 | Imports `data.StockSymbol` |
| domain  | `domain/ObserveConnectionStateUseCase.kt` | 3 | Imports `ui.feed.ConnectionState` |
| data    | `data/PriceFeedRepositoryImpl.kt` | 4 | Imports `ui.feed.ConnectionState` |
| data    | `data/WebSocketService.kt` | 5 | Imports `ui.feed.ConnectionState` |
| ui      | `ui/feed/FeedViewModel.kt` | 6–8 | Imports `data.PriceDirection`, `data.PriceUpdate`, `data.buildInitialStocks` |
| ui      | `ui/feed/FeedScreen.kt` | 46–48 | Imports `data.PriceDirection`, `data.StockSymbol` |
| ui      | `ui/feed/FeedUiState.kt` | 3 | Imports `data.StockSymbol` |
| ui      | `ui/feed/priceDirection.kt` | 3 | Imports `data.PriceDirection` |
| ui/detail | `ui/detail/DetailViewModel.kt` | 7–9, 14 | Imports `data.*`, `ui.feed.ConnectionState` |
| ui/detail | `ui/detail/SymbolDetailScreen.kt` | 44–46 | Imports `data.PriceDirection`, `data.StockSymbol`, `ui.feed.ConnectionState` |

## Summary

- **Domain** must not depend on `data` or `ui`: repository and use cases currently use `PriceUpdate`, `StockSymbol`, `ConnectionState` from outer layers.
- **Data** must not depend on `ui`: `WebSocketService` and `PriceFeedRepositoryImpl` use `ui.feed.ConnectionState`.
- **Presentation** may depend on domain; currently it also depends on `data` types (`StockSymbol`, `PriceUpdate`, `PriceDirection`, `buildInitialStocks`) and `ui.feed.ConnectionState` in ViewModels/screens.
