# UWB Indoor Positioning

## An app that provides ranging and indoor positioning of UWB-capable Android devices 

This project is an Android app that uses Android's new UWB API to demonstrate how Ultra-Wideband can be used to track the location of other Android devices. This is especially relevant for indoor locations, where GPS might not provide enough location accuracy. While GPS on smartphones is usually accurate to within a radius of 4.9 meters under ideal conditions, the accuracy can decrease significantly inside of buildings, potentially dropping to only 10-20 meters. 

This app differs from most UWB tracking systems, because instead of requiring three UWB anchors, this app requires just one. In total, the app needs at least two UWB-capable Android devices in order to work, one acting as a UWB responder and the other one as a UWB anchor. Anchors are stationary Android devices that broadcast their position to moving Android devices, so-called responders. Using an anchor's fixed position, this app can calculate the coordinates of responders within the anchor's range, allowing for significantly more accurate coordinates compared to GPS. This is done by using the distance and azimuth between the anchor and responder and calculating the offset relative to the anchor's location. Using this method, the accuracy of the app's calculated coordinates is within a few centimeters.

Responders are able to see their distance, azimuth and elevation relative to the anchor, as well as an arrow pointing in the direction of the anchor. Additionally, responders can view their precise coordinates and compare them to GPS coordinates on Google Maps.

> [!IMPORTANT]
> For this app to work properly, the following requirements must be met:
> * Both the anchor and responder must be UWB-capable Android devices that also support UWB ranging.
> * The anchor and responder must be held in a way, so that their antennas used for UWB are facing in their intended direction (e.g., phones should usually be held in portrait mode).
> * Ideally, the anchor is placed upright onto a wall. In any case, it is important that responders do not move past the anchor device.
> * In order to calculate correct responder coordinates, the responders must have the same compass bearing as the anchor. The reason for this is given under [Known issues](#known-issues).

## Screenshots
<picture>
  <source media="(prefers-color-scheme: dark)" srcset="https://github.com/user-attachments/assets/aae8bae3-e87d-4060-a60b-341688bf5dae" width="300">
  <source media="(prefers-color-scheme: light)" srcset="https://github.com/user-attachments/assets/aea1acbc-7477-4e23-85eb-7c3a23dc1f08" width="300">
  <img alt="Shows the responder's UWB ranging screen." src="https://github.com/user-attachments/assets/aea1acbc-7477-4e23-85eb-7c3a23dc1f08" width="300">
</picture>

<picture>
  <source media="(prefers-color-scheme: dark)" srcset="https://github.com/user-attachments/assets/fd415d3e-71a4-4301-9e37-2ac0d323f517" width="300">
  <source media="(prefers-color-scheme: light)" srcset="https://github.com/user-attachments/assets/0b8cf381-707c-4b69-a324-c37c4dd90c9a" width="300">
  <img alt="Shows the responder's location screen, displaying its coordinates and location on a map." src="https://github.com/user-attachments/assets/0b8cf381-707c-4b69-a324-c37c4dd90c9a" width="300">
</picture>

<br>

<picture>
  <source media="(prefers-color-scheme: dark)" srcset="https://github.com/user-attachments/assets/3ca123a0-2c51-4d4f-b32d-5b9e32fe8438" width="300">
  <source media="(prefers-color-scheme: light)" srcset="https://github.com/user-attachments/assets/18133bce-104b-4726-ac9d-14ab60929d15" width="300">
  <img alt="Shows the anchor's location input screen." src="https://github.com/user-attachments/assets/18133bce-104b-4726-ac9d-14ab60929d15" width="300">
</picture>

<picture>
  <source media="(prefers-color-scheme: dark)" srcset="https://github.com/user-attachments/assets/bbb23087-4b5f-4b74-a749-03d42f8a7485" width="300">
  <source media="(prefers-color-scheme: light)" srcset="https://github.com/user-attachments/assets/8621ca0a-87f9-4802-a43c-86d885c31e63" width="300">
  <img alt="Shows the anchor's UWB ranging screen." src="https://github.com/user-attachments/assets/8621ca0a-87f9-4802-a43c-86d885c31e63" width="300">
</picture>

<br>
<br>

**Note:** Whether you see the light mode or dark mode screenshots of the app depends on your device's theme.

## How to install and run this app on your Android device

1. Install Android Studio and `clone` this project
2. Generate a Google Maps API key
3. In the project's root directory that also contains `local.defaults.properties`, create a file called `secrets.properties`
4. Paste the following line of code into `secrets.properties` and replace `YOUR_API_KEY` with your own API key: `MAPS_API_KEY=YOUR_API_KEY`
5. Build the APK
6. Install the APK on your Android device

## Known issues
The azimuth provided by Android is in the range (-90, 90] and is affected by the responder's compass bearing. If the responder has a different compass bearing than the anchor, the azimuth either decreases or increases. This is an issue, because the azimuth is needed to determine the responder's location relative to the anchor. Thus, having a different compass bearing than the anchor can distort the coordinate calculation. Sadly, there is no way to circumvent this, because the azimuth's values are limited to the range (-90, 90], making it difficult to distinguish between the responder's compass bearing and the actual angle between the two devices. For example, picture an anchor with a compass bearing of 0°. Now picture two responders with a compass bearing of 270°, one in front of the anchor, the other one to the left of the anchor. Both responders will receive an azimuth of 90° and there is no way to distinguish their actual angles relative to the anchor.


