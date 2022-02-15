
<p align="center">
<img height="200" src='https://github.com/fornewid/Neumorphism/blob/main/art/preview.png'/>
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
<img width="300" src="https://github.com/fornewid/Neumorphism/blob/main/art/preview_light.gif"/> <img width="300" src="https://github.com/fornewid/Neumorphism/blob/main/art/preview_dark.gif"/>
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
    implementation 'com.github.fornewid:neumorphism:{latest_version}'
}
```

## Features
- Draw a shadow background on widgets for Neumorphism.
  Supported on the following widgets:
  - ViewGroup: CardView
  - View: Button, FloatingActionButton, ImageView
- Draw a text shadow on TextView for Neumorphism.

> If you want more features or want to fix bugs, please click [new issue](https://github.com/fornewid/neumorphism/issues/new/choose) and report to me!

## Usage
There is a [sample](https://github.com/fornewid/neumorphism/tree/main/sample) provided which shows how to use the library:

```xml
<soup.neumorphism.NeumorphCardView
    // Pre-defined style
    style="@style/Widget.Neumorph.CardView"

    // Set shadow elevation and colors
    app:neumorph_shadowElevation="6dp"
    app:neumorph_shadowColorLight="@color/solid_light_color"
    app:neumorph_shadowColorDark="@color/solid_dark_color"

    // Set light source
    app:neumorph_lightSource="leftTop|leftBottom|rightTop|rightBottom"

    // Set shape type and corner size
    app:neumorph_shapeType="{flat|pressed|basin}"
    app:neumorph_shapeAppearance="@style/CustomShapeAppearance"

    // Set background or stroke
    app:neumorph_backgroundColor="@color/background_color"
    app:neumorph_strokeColor="@color/stroke_color"
    app:neumorph_strokeWidth="@dimen/stroke_width"

    // Use a inset to avoid clipping shadow. (default=12dp)
    app:neumorph_inset="12dp"
    app:neumorph_insetStart="12dp"
    app:neumorph_insetEnd="12dp"
    app:neumorph_insetTop="12dp"
    app:neumorph_insetBottom="12dp"

    // Use a padding. (default=12dp)
    android:padding="12dp">

    <!-- NeumorphCardView extends FrameLayout. So you can wrap childrens like this. -->
    <ConstraintLayout />
</soup.neumorphism.NeumorphCardView>

<style name="CustomShapeAppearance">
    <item name="neumorph_cornerFamily">{rounded|oval}</item>
    <item name="neumorph_cornerSize">32dp</item>

    <!-- Or if wants different radii depending on the corner. -->
    <item name="neumorph_cornerSizeTopLeft">16dp</item>
    <item name="neumorph_cornerSizeTopRight">16dp</item>
    <item name="neumorph_cornerSizeBottomLeft">16dp</item>
    <item name="neumorph_cornerSizeBottomRight">16dp</item>
</style>
```

- #### LightSource
| LEFT_TOP | LEFT_BOTTOM | RIGHT_TOP | RIGHT_BOTTOM |
| :--: | :-----: | :---: | :---: |
| <img width="100" src="https://github.com/fornewid/Neumorphism/blob/main/art/lightSource_leftTop.png"/> | <img width="100" src="https://github.com/fornewid/Neumorphism/blob/main/art/lightSource_leftBottom.png"/> | <img width="100" src="https://github.com/fornewid/Neumorphism/blob/main/art/lightSource_rightTop.png"/> | <img width="100" src="https://github.com/fornewid/Neumorphism/blob/main/art/lightSource_rightBottom.png"/> |

- #### ShapeType
| FLAT | PRESSED | BASIN |
| :--: | :-----: | :---: |
| <img width="100" src="https://github.com/fornewid/Neumorphism/blob/main/art/shape_flat.png"/> | <img width="100" src="https://github.com/fornewid/Neumorphism/blob/main/art/shape_pressed.png"/> | <img width="100" src="https://github.com/fornewid/Neumorphism/blob/main/art/shape_basin.png"/> |

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
