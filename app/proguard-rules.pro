
-repackageclasses 'com.gameassist.plugin'

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations code/removal/simple,code/removal/advanced
-ignorewarnings                # 抑制警告

-keep public class * extends android.view.View
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference

#Log相关全部移除
-assumenosideeffects class android.util.Log { public *; }

-keep public class * extends com.gameassist.plugin.Plugin

-keep public class  com.gameassist.plugin.speedchanger.NativeUtils {*;}


-keepclassmembers class * {    native <methods>;}


