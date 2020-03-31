
<p align="center">
<img height="200" src='https://github.com/fornewid/Neumorphism/blob/master/art/preview.png'/>
</p>

<h1 align="center">Neumorphism in Android</h1><br/>
<p align="center">
  This is the experimental codes to build Neumorphism designs in Android.<br/>
  Not a library. Just sample project now.<br/>
  <br/>
</p>
</br>

<p align="center">
<a href="https://opensource.org/licenses/Apache-2.0"><img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
<a href='https://developer.android.com'><img src='http://img.shields.io/badge/platform-android-green.svg'/></a>
<a href='https://jitpack.io/#fornewid/neumorphism'><img src='https://jitpack.io/v/fornewid/neumorphism.svg'/></a>
</p>
<br/>

<h2 align="center">Preview</h2>
<h4 align="center">Light    |    Dark</h4>
<p align="center">
<img width="300" src="https://github.com/fornewid/Neumorphism/blob/master/art/preview_light.png"/> <img width="300" src="https://github.com/fornewid/Neumorphism/blob/master/art/preview_dark.png"/>
</p>

## Dependency

Add this in your root `build.gradle` file (**not** your module `build.gradle` file):

```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

Then, add the library to your module `build.gradle`
```gradle
dependencies {
    implementation 'com.github.fornewid:neumorphism:0.1.0'
}
```

## Features
- CardView
- FloatingActionButton
- TextView

> If you want more features, please click [new issue](https://github.com/fornewid/neumorphism/issues/new) and report to me!

## Usage
There is a [sample](https://github.com/fornewid/neumorphism/tree/master/sample) provided which shows how to use the library:

- CardView
```xml
<soup.neumorphism.NeumorphCardView
    ...
    app:neumorph_shapeType="{flat|pressed}"
    app:neumorph_shadowElevation="6dp"
    app:neumorph_shadowColorLight="@color/solid_light_color"
    app:neumorph_shadowColorDark="@color/solid_dark_color" />
```

- FloatingActionButton
```xml
<soup.neumorphism.NeumorphFloatingActionButton
    ...
    app:neumorph_shadowElevation="6dp"
    app:neumorph_shadowColorLight="@color/solid_light_color"
    app:neumorph_shadowColorDark="@color/solid_dark_color" />
```

- TextView
```xml
<soup.neumorphism.NeumorphTextView
    ...
    android:text="Neumorphism"
    android:textColor="@color/background"
    android:textSize="40sp"
    android:textStyle="bold" />
```

## License

```
Copyright 2020 SOUP

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements. See the NOTICE file distributed with this work for
additional information regarding copyright ownership. The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
```
