# Data class and type naming by layer

Use these suffixes so you can tell at a glance which layer a type belongs to.

| Layer      | Suffix   | Example                    | When to use |
|-----------|----------|----------------------------|-------------|
| **Domain** | **`Entity`** | `StockSymbolEntity`, `PriceUpdateEntity`, `ConnectionStateEntity`, `PriceDirectionEntity` | Entities and value objects in `domain/model/`. Use `Entity` suffix for domain models. |
| **Data**   | **`Dto`** | `StockSymbolDto`, `PriceUpdateDto`, `PriceDirectionDto` | Data transfer / wire / parsed models in `data/`. API responses, DB rows, parsed WebSocket payloads. |
| **UI**     | **`Ui`**  | `FeedItemUi`, `ConnectionStateUi`, `FeedUiState`, `DetailUiState` | Screen state and display models in `ui/`. Only presentation layer. |

## Notes

- **Domain**: Use `Entity` suffix for domain models; they represent core business concepts and live in `domain/model/`.
- **Data**: `Dto` = Data Transfer Object. Use for anything that crosses the data boundary (network, DB, file) before mapping to domain.
- **UI**: `Ui` = shown to the user. ViewModels map domain → Ui types; Composables use only Ui types.
- **Core** (e.g. `AppException`, `DataResponse`): No suffix required; use clear descriptive names.
- **Data result wrappers** (e.g. `DataResponse<T>`): Keep descriptive names; they are not DTOs.
- **Sealed classes / enums**: Same rule per package — domain has `ConnectionStateEntity`, data has `PriceDirectionDto`, ui has `ConnectionStateUi`, `PriceDirectionUi`.
