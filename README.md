# 📸 QR Code Scanner App with Print and Share Functionality

![License](https://img.shields.io/badge/license-MIT-blue.svg)  
![API Level](https://img.shields.io/badge/API-21%2B-blue.svg)  
![Android Studio](https://img.shields.io/badge/Android%20Studio-4.2%2B-brightgreen.svg)  

The **QR Code Scanner App** is a powerful Android application that allows users to:
- Generate QR codes dynamically based on input.
- Customize QR code colors.
- Save, share, and print generated QR codes.
- Pair and unpair with Bluetooth printers for seamless printing.

---

## 🎯 **Features**

✅ **QR Code Generation**  
- Automatically generates QR codes based on user input.
- Supports real-time generation and updates when text changes.

🎨 **Color Customization**  
- Choose custom colors for the QR code using a color picker.

📸 **Save & Share**  
- Save generated QR codes to the device’s storage.
- Share QR codes via different platforms (Email, Messaging, etc.).

🖨️ **Print to Bluetooth Printer**  
- Connect and print QR codes directly to paired Bluetooth printers.
- Supports unpairing and re-pairing of devices.

💾 **Dynamic Size Adjustment**  
- Resize QR codes dynamically based on user input.

---

## 📚 **Table of Contents**

- [Features](#-features)
- [Installation](#-installation)
- [Usage](#-usage)
- [Code Structure](#-code-structure)
- [Permissions](#-permissions)
- [Dependencies](#-dependencies)
- [License](#-license)

---

## ⚙️ **Installation**

1. **Clone the Repository:**
```bash
git clone https://github.com/potatoscript/QRCodeScannerApp.git
```

2. **Open Project in Android Studio:**
- Open `Android Studio`.
- Select `Open an existing project` and navigate to the cloned repository.

3. **Build & Run the Project:**
- Connect an Android device or start an emulator.
- Click on `Run` (▶️) to build and launch the app.

---

## 🚀 **Usage**

### 1. **Generate QR Code**
- Enter text in the input field.
- The QR code is generated automatically.

### 2. **Customize QR Code Color**
- Click on the `Change Color` button.
- Select a custom color from the color picker.

### 3. **Save and Share QR Code**
- Click the save button to store the QR code as an image.
- Click the share button to send the image using available apps.

### 4. **Print QR Code**
- Pair the app with a Bluetooth printer.
- Send the QR code directly to the paired printer.

---

## 📂 **Code Structure**

```
/QRCodeScannerApp
├── /app
│   ├── /src
│   │   ├── /main
│   │   │   ├── /java/com/potato/barcodescanner
│   │   │   │   ├── MainActivity.java  // Main activity with menu options
│   │   │   │   ├── QRcodeActivity.java  // Handles QR code generation
│   │   │   │   ├── ColorPickerActivity.java  // Color selection logic
│   │   │   │   └── /utils
│   │   │   │       └── QRCodeUtils.java  // QR code utility methods
│   │   │   ├── /res
│   │   │   │   ├── /layout
│   │   │   │   ├── /drawable
│   │   │   │   └── /values
│   │   └── /AndroidManifest.xml  // App configuration and permissions
├── /gradle
└── build.gradle
```

---

## 🛡️ **Permissions**

The following permissions are required:

```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
```

---

## 📦 **Dependencies**

Add the following dependencies in your `app/build.gradle`:

```groovy
dependencies {
    implementation 'com.google.zxing:core:3.4.1'  // QR Code generation
    implementation 'com.journeyapps:zxing-android-embedded:4.3.0'  // Barcode scanning
    implementation 'com.github.mazenrashed:Printooth:1.0.6'  // Bluetooth printing
    implementation 'com.squareup.picasso:picasso:2.71828'  // Image loading and caching
}
```

---

## 📝 **Configuration**

### Bluetooth Printing Setup
To enable Bluetooth printing:
1. Enable Bluetooth on your device.
2. Pair with a compatible Bluetooth printer.
3. Use the pairing option in the app to connect to the printer.

### Customizing QR Code Color
- Open `ColorPickerActivity.java` to modify the default color.
- Adjust `r`, `g`, and `b` values to set custom colors.

---

## 🔥 **Troubleshooting**

1. **Bluetooth Printer Not Found:**  
   - Ensure Bluetooth is enabled and the printer is in pairing mode.

2. **QR Code Not Generating:**  
   - Verify that text input is not empty.

3. **Permission Denied Errors:**  
   - Ensure that necessary permissions are granted in `AndroidManifest.xml`.

---

## 🧩 **Future Enhancements**

✅ Support for Barcode Generation  
✅ Add QR Code History  
✅ Improve UI/UX with Material Design  
✅ Implement Dark Mode  

---

## 📜 **License**

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## 📬 **Contact**

💡 Developed by [PotatoScript](https://github.com/potatoscript)  
📧 For inquiries, email: `contact@potatoscript.dev`

