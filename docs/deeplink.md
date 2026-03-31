# PRISM X Android deeplink

<!--- TOC -->

* [Introduction](#introduction)
  * [Asset Links](#asset-links)
  * [Supported links](#supported-links)
* [Developer tools](#developer-tools)

<!--- END -->


## Introduction

PRISM X Android supports deep linking to specific screens in the application. This document explains how to use deep links in PRISM X Android.

### Asset Links

The asset links file is available at https://prism.io/.well-known/assetlinks.json

### Supported links

PRISM Call link: 
> https://call.prism.io/Example

Link to a user:
> https://app.prism.io/#/user/@alice:prism.org

Link to a room by id or alias:
> https://app.prism.io/#/room/!roomid:prism.org
> https://app.prism.io/#/room/#prism-x-android:prism.org

Link to a room with a specific event:
> https://app.prism.io/#/room/!roomid:prism.org/$eventid

Note that it will also work with other domain such as:
> https://mobile.prism.io
> https://develop.prism.io
> https://staging.prism.io

## Developer tools

Using an Android 12 or higher emulator

Ensure links verification is enabled
```bash
adb shell am compat enable 175408749 io.prism.android.x.debug  
```

Reset link verifications for the given package id
```bash
adb shell pm set-app-links --package io.prism.android.x.debug 0 all 
```

Force the package id links to be verified
```bash
adb shell pm verify-app-links --re-verify io.prism.android.x.debug 
```

Print the link verification of the package id
```bash
adb shell pm get-app-links io.prism.android.x.debug
```

```
  io.prism.android.x.debug:
    ID: e2ece472-c266-4bf0-829c-be79959a6270
    Signatures: [B0:B0:51:DC:56:5C:81:2F:E1:7F:6F:3E:94:5B:4D:79:04:71:23:AB:0D:A6:12:86:76:9E:B2:94:91:97:13:0E]
    Domain verification state:
      *.prism.io: 1024
```
