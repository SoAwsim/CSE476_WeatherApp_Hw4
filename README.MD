# Yeditepe University CSE 476 Weather App Assignment 4

In this assignment we were expected to implement a simple weather app converter using the OpenWeatherMap
JSON APIs.

I have implemented MVC architecture using ViewModels like in my previous assignment but this time I also
made use dependency injection using Android Hilt library .

I have some edge cases here such not able to switch to a chosen location instead of the current location
without restarting the app and I should not be handling the case where the user rejects the location permission
I might fix these bugs later on if I have some spare time.

## Features
- Has Internet access and can query web services for weather conditions.
- Has easy to use layout with four user interfaces (a) today’s temperature for a
location (b) temperature for a specific location for the next week (c) wind
forecasts (d) settings, where the user can select location by name, GPS or
Google locate and other settings that you may need (Celsius/Fahrenheit).
- Forecasts data could be retrieved from the OpenWeatherMap - One Call API
3.0 Free Tier web services (https://openweathermap.org/) which also provides
examples on its use with JSON and HTTP GET/POST methods – after you
register with the web site.
- Downloads and caches images multithreaded just like the currency convert assignment.

## Running
You need an API key from https://openweathermap.org/. This API key should be stored in the apikey.properties
file which is generated during the configuration phase of the build.gradle script. The template for this
file is available in the apikey.properties.template file which the gradle script is going to generate a apikey.properties
from the content of this file. Once the file is generated you can write your API key into this file.

After the file is generated put your API key into the apikey.properties file as that is the file which
the build script reads from. It is also ignored by git therefore you won't be able accidentally push
this file by default.

Keep in mind that Android Studio might hide other source file since the default file layout is set as
Android, you need to change that to the Project to see all of the files under your project's directory.
You might also need to reload files from the disk after the initialization finishes since by default
apikey.properties might not show up in the Android Studio.

Clone the repo:
```bash
git clone https://github.com/SoAwsim/CSE476_WeatherApp_Hw4
```

Put your API key to the apikey.properties
```bash
API_KEY=YOUR_API_KEY
```

Then build using Android Studio.
