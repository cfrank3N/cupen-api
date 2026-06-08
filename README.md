# cupen-api
Api that handles data collected from a yearly tournament between friends

## Developing
clone the repository
cd into cupen-api
```./gradlew bootDev```
This starts the API in devmode and automatically starts a DB in docker with testcontainers.

You need to have a Cloudinary media library set up.
Then you need to set these env variables on the machine:

```
CLOUDINARY_API_KEY
CLOUDINARY_API_SECRET
CLOUDINARY_CLOUD_NAME
```
You can get them from cloudinary when you've set up a media library.
