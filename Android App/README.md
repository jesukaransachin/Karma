# Karma

![Android CI](https://github.com/jesukaransachin/HelpMe/workflows/Android%20CI/badge.svg?branch=master)


## Setup

1. Clone this repository and import ```Android App``` into Android Studio.
2. Add Google Maps API keys:
   - [Get a Maps API key](https://developers.google.com/maps/documentation/android-sdk/get-api-key)
   - Create a file in the root directory called secure.properties (this file should NOT be under version control to protect your API key)
   - Add a single line to secure.properties that looks like MAPS_API_KEY=YOUR_API_KEY, where YOUR_API_KEY is the API key you obtained in the first step
3. [Add Firebase Cloud Messaging API configs](https://firebase.google.com/docs/android/setup#add-config-file)

