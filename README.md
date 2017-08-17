# Garden Lights Controller
A simple Android application written in Kotlin to control the garden light through REST API server provided by Raspberry Pi. 

**Description:**

- IP detection through wifi network discovery
- state indicator
- constraint layout
- min. API: 19
- REST client expecting basic GET requests on port 3000

**Set of supported commands:** 

/on to turn the light on
/off to turn the light off
/hostname to obtain the server hostname
/state to report server state which could be either "0" or "1"

To build, run: 

```bash
./gradlew clean build
```

To execute tests:

```bash
./gradlew test
```

