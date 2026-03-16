# Dark Pattern Detector — Android App

A privacy-first Android app that detects manipulative design patterns in screenshots and websites using on-device AI (Gemini Nano), with zero sensitive data leaving your device.

## Key Features

- **Screenshot Analysis**: Share a screenshot directly to the app for instant analysis.
- **Website Analysis**: Enter a URL to analyze live website content without taking screenshots.
- **Dual-Engine Detection**: Uses **Gemini Nano** for high-accuracy AI analysis, with a fast **Regex Fallback** for unsupported devices.
- **Privacy-First**: Analysis is performed 100% on-device. Screenshots are held in memory only and never saved to disk.

## How It Works

### Via Screenshots
1. 📸 Take a screenshot of a suspicious app or website.
2. 📤 Tap **Share → Dark Pattern Detector**.
3. 🧠 The app analyzes the visual elements **on-device**.
4. 📊 View detected patterns with confidence scores.

### Via URL
1. 🔗 Paste a website URL into the app's home screen.
2. 🌐 The app loads the site in a local WebView and extracts text context.
3. 🧠 Gemini Nano processes the text to identify deceptive language.

## Privacy

- ✅ **100% On-Device**: AI models run locally via Android's AICore.
- ✅ **Ephemeral Storage**: Screenshots are never written to disk or sent to a server.
- ✅ **Secure Web Loading**: `INTERNET` permission is used strictly to load the requested URL in a local WebView for text extraction. No browsing data or history is collected.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin (2.2.10) |
| UI | Jetpack Compose (Material 3) |
| AI (Multimodal) | ML Kit GenAI (Image Description) |
| AI (Text) | ML Kit GenAI (Prompt API) |
| Fallback | ML Kit OCR + Pattern Matching |
| Build System | Gradle 8.7.3 + Compose Compiler |

## Building

1. Open the project in **Android Studio**.
2. Ensure you have the **Android SDK 35** and **Kotlin 2.2.10** configured.
3. Sync Gradle and run on a supported device (e.g., Pixel 8/9, Galaxy S24) or emulator.
4. **Note**: On some devices, you may need to enable "AICore Settings" in Developer Options to trigger the Gemini Nano model download.

## Dark Patterns Detected

| Category | Example |
|----------|---------|
| Fake Urgency | "Offer ends in 00:05:00!" |
| Fake Scarcity | "Only 2 left in stock!" |
| Fake Social Proof | "15 people viewing this" |
| Confirmshaming | "No thanks, I hate saving money" |
| Hidden Costs | "Service fee: $4.99" |
| Hidden Subscription | "Free trial, then $9.99/month" |
| Nagging | "You left items in your cart!" |
| Hard to Cancel | "Call to cancel your subscription" |
| Preselection | "☑ Sign me up for emails" |
| Trick Wording | "Uncheck to not receive emails" |
| Forced Action | "Sign up to continue reading" |
| Sneaking | "Insurance added to cart" |

## License

MIT
