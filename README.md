# Web2Wave

Web2Wave is a lightweight Java package that provides a simple interface for managing user subscriptions and properties through a REST API.

## Features

- Fetch subscription status for users
- Check for active subscriptions
- Manage user properties
- Set third-parties profiles
- Thread-safe singleton design
- Built-in error handling

## Installation

### Gradle Installation

Add the following to your `settings.gradle` file:

```java

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

In you app-level `build.gradle` file:

```java
dependencies {
        implementation "com.github.web2wave:web2wave_java:1.0.0"
}
```

## Setup

Before using Web2Wave, you need to configure the base URL and API key:

```java
Web2Wave.getInstance().initWith("your-api-key");
```

## Usage

The library works with the network, make sure that the calls are not made on the main thread.

### Checking Subscription Status

```java

    // Fetch subscriptions
    List<Map<String, Object>> status = Web2Wave.getInstance().fetchSubscriptions("user123");

    // Check if user has an active subscription
    boolean isActive = Web2Wave.getInstance().hasActiveSubscription("user123");

```

### Managing User Properties

```java
    // Fetch user properties
    Map<String, String> properties = Web2Wave.getInstance().fetchUserProperties("user123");
    System.out.println("User properties: "  +  properties.toString());



    // Update a user property
    Result<Boolean> result = Web2Wave.getInstance().updateUserProperty("user123", "preferredTheme", "dark");
    if (result.isFailure()){
            System.out.println("Failed to update property: " + result.getError().getLocalizedMessage());
        } else  {
            System.out.println("Property updated successfully");
        }

```

### Managing third-party profiles
```java

    // Save Adapty profileID
    Result<Boolean> result = Web2Wave.getInstance().setAdaptyProfileID("user123", "{adaptyProfileID}");

    if (result.isFailure()){
            System.out.println("Failed to save profileID: " + result.getError().getLocalizedMessage());
        } else  {
            System.out.println("ProfileID saved");
        }

    // Save Revenue Cat profileID
    Result<Boolean> revResult = Web2Wave.getInstance().setRevenuecatProfileID("user123", "{revenueCatProfileID}");

    // Save Qonversion profileID
    Result<Boolean> qonversionResult = Web2Wave.getInstance().setQonversionProfileID("user123", "{qonversionProfileID}");

```

## API Reference

### `Web2Wave.getInstance()`

The singleton instance of the Web2Wave client.

### Methods

#### `public Map<String, Object> fetchSubscriptionStatus(String appUserID)`
Fetches the subscription status for a given user ID.

#### `public List<Map<String, Object>> fetchSubscriptions(String appUserID String)`
Fetches all subscriptionsfor a given user ID.

#### `public boolean hasActiveSubscription(String appUserID)`
Checks if the user has an active subscription (including trial status).

#### `public Map<String, String> fetchUserProperties(String: appUserID)`
Retrieves all properties associated with a user.

#### `public Result<Boolean> updateUserProperty(String appUserID, String property, String value)`
Updates a specific property for a user.

#### `public Result<Boolean> setRevenuecatProfileID(String appUserID, String revenueCatProfileID)`
Set Revenuecat profileID

#### `public Result<Boolean> setAdaptyProfileID(String appUserID, String adaptyProfileID)`
Set Adapty profileID

#### `public Result<Boolean> setQonversionProfileID(String appUserID, String qonversionProfileID)`
Set Qonversion ProfileID

## License

MIT

## Author

Aleksandr Filpenko
