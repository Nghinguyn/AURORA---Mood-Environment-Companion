# AURA – The AI Mood & Environment Companion

**AURA** is an Android application designed to help users understand and improve their mental well-being by connecting their mood with their environment.  
Using **AI** and the device’s **sensors**, AURA observes emotional cues and environmental factors—like sound, light, and location—to deliver short, uplifting reflections or positive facts inspired by the user’s surroundings.

For example:
- Near a **river**? AURA might share that spending time near water can reduce stress.
- Sitting in a **park**? It could highlight how trees and greenery can boost your mood.

By providing **context-aware**, encouraging insights, AURA aims to:
- Promote **mindfulness**
- Reduce **stress**
- Help users feel more **connected** to both their emotions and the world around them.

---

## App Structure & Planned Layouts

### 1. Home / Mood Check Page
- Users enter their current mood using emoji or short text.
- Displays a **dynamic sky visualization** that reflects the user’s emotion and environmental context.

### 2. Environmental Scan & AI Reflection Page
- Uses sensors (microphone, light, GPS) to gather real-time environmental data.
- AI generates a **positive, location-based fact or reflection** tailored to the user’s current mood and surroundings.
- If location data isn’t available, AURA provides a **personalized story, reflection, or suggestion**.

### 3. Journey Log Page
- Stores past moods, sensor data, and AI reflections.
- Displays entries as **cards or charts** to visualize emotional trends over time.

**Optional:**  
- **Settings / Customization Page** – for adjusting visual themes, preferences, and notification options.

---

## Sensor Usage

| Sensor | Purpose / Usage |
|:--|:--|
| **Microphone** | Detect ambient sound levels (quiet, moderate, loud) |
| **Light Sensor** | Measure brightness of the surrounding environment |
| **GPS** | Determine general location type (urban, park, near water) |
| **Gyroscope / Accelerometer** | Animate sky visualization based on device movement |

---

## Data Storage

- **Room Database:** Used to store mood entries, AI reflections, and sensor readings locally.  
- Enables **visualization of emotional trends** and a personalized “journey log.”  
- No external cloud storage is required for this project, ensuring privacy and offline access.

---

## Tech Stack (Planned)
- **Platform:** Android (Kotlin / Java)
- **AI Integration:** OpenAI or custom local model
- **Database:** Room (SQLite)
- **UI Framework:** Jetpack Compose / XML
- **Sensors API:** Android SensorManager, Location Services

---

## Project Aim
AURA is not just an app—it’s a **companion for emotional awareness**.  
Through the thoughtful combination of **AI, environmental sensing, and self-reflection**, AURA encourages users to pause, breathe, and appreciate the connection between their **inner mood** and the **outer world**.

---

## Future Enhancements
- Mood prediction using historical data and context.
- Integration with wearable devices for richer sensor inputs.
- Cloud sync (optional) for backup and cross-device journaling.
- Community reflections and shared positivity feed.

---

**Developed with great care and passion to promote mindfulness, positivity, and emotional connection.**

