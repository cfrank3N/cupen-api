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
export CLOUDINARY_API_KEY=<your-api-key>
export CLOUDINARY_API_SECRET=<your-api-secret>
export CLOUDINARY_CLOUD_NAME=<your-cloudinary-cloud-name>
```
You can get them from cloudinary when you've set up a media library.
