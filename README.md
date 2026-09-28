# Kitabu - MADB372 SF1

Kitabu is a native Android Library Book Rental & Reservation System developed for MADB372 SF1. The app uses Kotlin, Jetpack Compose (Material 3), Room Database, MVVM, Kotlin Coroutines, Flow/StateFlow and Navigation Compose.

## Core features
- Responsive book catalog using `LazyVerticalGrid`.
- Availability badges for available vs borrowed/reserved books.
- Dynamic SQL `LIKE` search by title or author.
- Catalog filter chips for All / Available / Borrowed.
- Reservation dialog with 7, 14 or 21 day rental duration.
- User reservations dashboard using `LazyColumn`.
- Reservation date, return deadline and calculated days remaining.
- Pending reservation pickup/activation and cancellation.
- Active rental renewal (+7 days) and return.
- Reactive UI updates from Room `Flow`.
- Coroutines via `viewModelScope.launch` for database writes.
- Room pre-population with sample academic books.

## Architecture

```
Compose UI (ui/)
        |
        v
KitabuViewModel (StateFlow + viewModelScope)
        |
        v
LibraryRepository (domain/repository)
        |
        v
OfflineLibraryRepository (data/repository)
        |
        v
Room DAOs -> BookEntity / BookingEntity -> SQLite
```

## Package structure
- `ui/` - Compose screens, reusable components, navigation and ViewModel.
- `domain/` - app enums/models and repository contract.
- `data/` - Room entities, DAOs, database and repository implementation.
- `di/` - lightweight dependency container.

## CRUD mapping
- **Create:** reserve a book -> inserts `BookingEntity` and marks `BookEntity.isAvailable = false`.
- **Read:** observe/search books and join current bookings with book details using Room + Flow.
- **Update:** activate booking, renew return deadline, process return and restore availability.
- **Delete:** cancel a pending reservation and delete its `BookingEntity`.

## Setup
1. Open the `Kitabu_MADB372_SF1` folder in Android Studio (Ladybug or newer).
2. Use JDK 17.
3. Let Gradle sync and download dependencies.
4. Run on an emulator/device with Android API 26+.
5. If Android Studio proposes newer compatible Gradle/Kotlin/KSP versions, update them together.

## Screenshots required for submission
Capture your own screenshots after running the app and place them in `StudName_MADB372_SF1.docx`:
1. Catalog with sample books.
2. Search results.
3. Available/Borrowed filter.
4. Reservation dialog.
5. Dashboard with pending reservation.
6. Dashboard with active rental.
7. Renew action result.
8. Return action + catalog book becoming Available again.
9. Cancel action + catalog book becoming Available again.

## GitHub
Create a **public** GitHub repository for the completed project and paste the repository URL into the submission document.

## Academic integrity / AI evidence
The assignment allows AI assistance but requires evidence of prompts and outputs. Add screenshots of the relevant AI conversations to the submission package/document as instructed by your lecturer.
