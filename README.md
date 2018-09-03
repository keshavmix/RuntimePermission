# RuntimePermission Library

This is one of the most easy to use and developer friendly library for Android.

## Integration
Add the below in your root build.gradle at the end of repository

##### Step 1. Add the JitPack repository to your build file 
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
  ##### Step 2. Add the dependency
 	dependencies {
	        implementation 'com.github.keshavmix:RuntimePermission:1.0'
	}
	
### Usage

You can create an instance of `PermissionManager` in your kotlin or java file in order to add runtime permissions.

### Kotlin sample
Step 1: Declare member variable for PermissionManager

```kotlin
    lateinit var mPermissionManager: PermissionManager
```

Step 2: Initialize the PermissionManager in onCreate method of your activity with Context of activity & required permissions.

```kotlin
    override fun onCreate(savedInstanceState: Bundle?) {
        ...
        mPermissionManager = PermissionManager(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                     Manifest.permission.CAMERA))
    }
```

Step 3: Set OnPermissionResultListener to PermissionManager to receive callbacks.

```kotlin
        mPermissionManager.setPermissionListener(object : OnPermissionResultListener {
            override fun onPermissionGranted() {

                //code to execute
                
            }

            override fun onPermissionDenied(permissions: ArrayList<String>) {
                //show alert dialog to ask permission again.
               
                AlertDialog.Builder(this@MainActivity)
                        .setMessage(R.string.permission_required)
                        .setPositiveButton(R.string.grant, DialogInterface.OnClickListener { dialogInterface, i -> mPermissionManager.checkAndRequestPermissions() })
                        .setNegativeButton(android.R.string.cancel, null)
                        .setCancelable(false)
                        .show()

            }

            override fun onPermissionBlocked(permissions: ArrayList<String>) {
                //Permission was denied and user checked Do not ask again.
                

            }
        })

        mPermissionManager.checkAndRequestPermissions()

    }
```
