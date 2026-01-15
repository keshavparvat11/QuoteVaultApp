

# 📱 QuoteVault – Quote Discovery App

**QuoteVault** is a modern Android application designed for discovering, saving, and sharing inspirational quotes. Built with a robust tech stack and backed by Supabase, it offers a seamless experience with cloud synchronization and deep personalization.

*This project was developed as part of a Mobile Application Developer Assignment, focusing on **Clean Architecture** and the **strategic use of AI tools** to accelerate the development lifecycle.*

---

## ✨ Features

### 🔐 Authentication (Supabase Auth)

* **Secure Access:** Email & password signup/login.
* **Session Management:** Persistent login sessions with auto-redirect to Home.
* **Recovery:** Integrated password reset flow.

### 📚 Quote Browsing & Discovery

* **Categorized Feeds:** Browse by Motivation, Love, Success, Wisdom, Humor, and more.
* **Smart Search:** Find quotes by specific text or your favorite authors.
* **Polished UX:** Pull-to-refresh functionality with graceful loading and empty states.

### ❤️ Favorites & Collections

* **Cloud Sync:** Favorites are synced to Supabase, ensuring your data follows you.
* **Management:** Easily add/remove quotes from a dedicated Favorites screen.

### 🌞 Daily Inspiration

* **Quote of the Day:** Featured quote on the Home screen.
* **Smart Notifications:** Daily reminders scheduled via `WorkManager` (toggleable in settings).
* **Home Widget:** Access the daily quote directly from your Android home screen.

### 🎨 Personalization & Settings

* **Theming:** Support for Light, Dark, and System default themes.
* **UI Tweaks:** Custom accent color selection and font size controls.
* **Persistence:** All preferences are saved locally using **Jetpack DataStore**.

---

## 🛠 Tech Stack

| Layer | Technology |
| --- | --- |
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose |
| **Architecture** | MVVM + Clean Architecture |
| **Backend** | Supabase (Auth & PostgREST) |
| **Dependency Injection** | Hilt |
| **Local Storage** | Room (Cache) & DataStore (Preferences) |
| **Background Tasks** | WorkManager |

---

## 🧠 AI Workflow

This project highlights the effective use of AI as a "thought partner" to speed up problem-solving:

* **Architectural Decisions:** Used AI to map out the transition from Firebase logic to Supabase.
* **Debugging:** Leveraged Gemini and ChatGPT for resolving complex Gradle serialization and Hilt injection errors.
* **SQL & Schema:** AI-assisted generation of Supabase table schemas and seed data (150+ quotes).

---

## 🗂 Project Structure

```text
app/
├── auth/               # Auth Screens & ViewModels
├── data/
│   ├── model/          # Data Entities
│   ├── local/          # Room DB & DataStore
│   ├── remote/         # Supabase API & DTOs
├── quotes/             # Repository Pattern implementation
├── Screens/            # Jetpack Compose UI Components
├── notification/       # WorkManager & Notification Logic
├── widget/             # Home Screen Widget implementation
└── di/                 # Hilt Modules

```

---

## ⚙️ Setup Instructions

1. **Clone the repository**
```bash
git clone https://github.com/keshavparvat11/QuoteVaultApp.git

```


2. **Supabase Configuration**
* Create a project at [supabase.com](https://supabase.com).
* Enable **Email Auth**.
* Create `quotes`, `favorites`, and `collections` tables.


3. **Local Environment**
Add the following to your `local.properties` (do not commit this file):
```properties
SUPABASE_URL=https://your-project-id.supabase.co
SUPABASE_ANON_KEY=your-public-anon-key

```


4. **Build**
Open in Android Studio, sync Gradle, and run on an emulator (API 24+).

---

## 🧪 Limitations & Future Scope

* **Widget:** Currently uses basic styling; planned update for RemoteViews refinement.
* **Notifications:** Time is currently fixed at 09:00; custom time-picker support is planned.
* **Sharing:** Expanding "Quote Card" templates with more background styles.

---

## 🎥 Demo

**[Watch the Loom Walkthrough]([https://www.google.com/search?q=ADD_YOUR_LINK_HERE](https://www.loom.com/share/ac7657cf7c6d4df984942bc6e2a018b2))**
*Includes: App demo, code overview, and AI workflow explanation.*

---



**Thank you for reviewing my assignment!
If you have questions, I’ll be happy to explain any part of the code.**
