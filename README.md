#Runtime Permission Library
[![](https://jitpack.io/v/keshavmix/RuntimePermission.svg)](https://jitpack.io/#keshavmix/RuntimePermission)
[![Platform](https://img.shields.io/badge/platform-android-blue.svg)](http://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-15%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=15)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Runtime%20Permission-orange.svg?style=flat)](https://android-arsenal.com/details/1/7097)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.html)

This is one of the most easy to use and developer friendly library to implement Runtime Permissions in Android apps. Runtime Permissions are introduced in Android Marshmallow (API level 23), where app will ask user for their permissions to access specific features. This library will also works in API level 23 and below. For this, you are still required to declare your permissions in AndroidManifest.xml.

###Installation

***Gradle**

	##### Step 1. Add it in your root build.gradle at the end of repositories:
	```gradle
  allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	```

	 ##### Step 2. Add the dependency in your app build.gradle
	```gradle
  dependencies {
	        implementation 'com.github.keshavmix:RuntimePermission:1.0'
	}
	```

***Maven**

	##### Step 1. Add the JitPack repository to your build file
	```gradle
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
	```

	##### Step 2. Add the dependency
	```gradle
  	<dependency>
	    <groupId>com.github.keshavmix</groupId>
	    <artifactId>RuntimePermission</artifactId>
	    <version>1.0</version>
	</dependency>

	```
	
###Usage
You can create an instance of `PermissionManager` in your kotlin or java file in order to add runtime permissions.

###Kotlin sample
#####Step 1:
Declare member variable for PermissionManager.

```kotlin
lateinit var mPermissionManager: PermissionManager
```

#####Step 2:
Initialize the PermissionManager in onCreate method of your activity with Context of activity.

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
	...
	mPermissionManager = PermissionManager(this)
}
```

or

Initialize the PermissionManager in onCreate method of your activity with Context of activity & array of required permissions.
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
	...
	mPermissionManager =  PermissionManager(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
					     Manifest.permission.CAMERA))
}
```

#####Step 3:
You can also add permissions using below method.
```kotlin 
mPermissionManager.addPermission(Manifest.permission.CAMERA)
```

Alternatively, you can set array of permissions using below method.

```kotlin
mPermissionManager.setPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
```

If you want to remove permission, simply use the below method.
```kotlin 
mPermissionManager.removePermission(Manifest.permission.CAMERA)
```

#####Step 4:
Set ```OnPermissionResultListener``` to PermissionManager to receive callbacks.	You are expected to write your code in ```onPermissionGranted()``` method as this method will be executed after user granted the required permissions. In case user denied the permissions, you are required to show alert dialog or snackbar to ask required permissions again with ```mPermissionManager.checkAndRequestPermissions()``` method. In case user opted 'Do not ask again' checkbox, you are also required to show an alert dialog in ```onPermissionBlocked``` method to inform and navigate user to settings screen to enable blocked permissions.

```kotlin
mPermissionManager.setPermissionListener(object : OnPermissionResultListener {
    override fun onPermissionGranted() {
	//code to execute
    }

    override fun onPermissionDenied(permissions: ArrayList<String>) {
	//show alert dialog to ask permission again.
    }

    override fun onPermissionBlocked(permissions: ArrayList<String>) {
	//Permission was denied and user checked Do not ask again. 
	//Inform and navigate user to settings screen to enable permissions.
    }
})
```

Alternatively, you can use the below method to pass function or block code to be executed when a specific callback received.
```kotlin
//set method to be executed when permission granted by user.
mPermissionManager.executeOnPermissionGranted { run { //code to execute } }

//set method to be executed when permission denied by user.
mPermissionManager.executeOnPermissionDenied { run { //show alert dialog to ask permission again. } }

//set method to be executed when permission blocked by user.
mPermissionManager.executeOnPermissionBlocked { run { //Permission was denied and user checked Do not ask again. 
//Inform and navigate user to settings screen to enable permissions. } }
```	

#### Important Notes:
Preference will be given to ```OnPermissionResultListener``` if implemented. 
If ```OnPermissionResultListener``` not implemented then default alert dialogs will be displayed by the PermissionManager in case of permission blocked or denied by user. 
However, you are still required to call ``` mPermissionManager.executeOnPermissionGranted { run { //code to execute } }``` in order to run your code.

##### Step 5:
Call ```onRequestPermissionsResult()``` method with same parameters ```(requestCode, permissions, grantResults)``` respectively which received in onRequestPermissionsResult method.
```kotlin
override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
	super.onRequestPermissionsResult(requestCode, permissions, grantResults)
	mPermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
}
```

#####Step 6:
Call ```onActivityResult()``` method with same parameter ```(requestCode)``` which is received in onActivityResult method of activity. Permissionmanager will handle and recheck when user returned from Settings screen after enabling required permissions.
```kotlin 
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //call OnActivityResult on permission manager to recheck when user returned from Settings screen.
        mPermissionManager.onActivityResult(requestCode)
}
```

#####Step 7:
Call ```checkAndRequestPermissions()``` method in onCreate or where ever you want to access features which require permissions.
```kotlin
mPermissionManager.checkAndRequestPermissions() 
```

#####Optional
You can enable or disable logs in PermissionManager using below line code. By default, logs are desiabled.
```kotlin
mPermissionManager.enableLogs(true) 
```

###Licence
 Copyright [2018] [Keshav Kumar Verma]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
limitations under the License.
