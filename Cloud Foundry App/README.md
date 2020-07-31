# KARMA


<p>
    <a href="https://cloud.ibm.com">
    <img src="https://img.shields.io/badge/IBM%20Cloud-powered-blue.svg" alt="IBM Cloud">
    </a>
    <img src="https://img.shields.io/badge/platform-node-lightgrey.svg?style=flat" alt="platform">
</p>


# Deploy Karma

## Steps

You can [deploy this application to IBM Cloud](https://cloud.ibm.com/developer/appservice/starter-kits/nodejs-express-app) or [build it locally](#building-locally) by cloning this repo first.
Make sure you have Cloudant and Cloud Object Storage setup on IBM or add them while using the click to deploy button above.

### Configuring API Keys

Before deploying to IBM Cloud you need to do the following steps:
- Setup FCM & Google Places api and replace the config files under ```server/config``` directory.
- Generate JWT token secrets and replace them in the ```manifest.xml``` file
- Setup Twilio and replace them in the ```manifest.xml``` file

Note: The above steps are for configuring when deploying to IBM cloud.

For trying out locally:
- Get the VCAP_SERVICES variable content from IBM Cloud -> Resource List -> CloudFoudry Apps -> Select the app -> Runtime -> Environment Variables -> VCAP_SERVICES
    - If you don't have Cloudant and Cloud Object Storage, please add them so that they reflect in the VCAP_SERVICES value. Fore more info on VCAP_SERVICES variable please visit [IBM](https://cloud.ibm.com/docs/watson?topic=watson-vcapServices)
- Setup FCM & Google Places api and replace the config files under ```server/config``` directory.
- Add the following environment variables to your OS or ```.env``` file. You may also require to logout and login or restart for the variables to reflect.
```
export VCAP_SERVICES="<IBM VCAP_SERVICES>"
export JWT_TOKEN_SECRET="<Jwt Token Secret>"
export JWT_REFRESH_TOKEN_SECRET="<Jwt Refresh Token Secret>"
export TWILIO_SID="<Your Twilio SID>"
export TWILIO_AUTH_TOKEN="<Your Twilio Auth Token>"
export TWILIO_PHONE="<Twilio Phone Number>"
```

### Deploying to IBM Cloud

<p align="center">
    <a href="https://cloud.ibm.com/developer/appservice/starter-kits/nodejs-express-app">
    <img src="https://cloud.ibm.com/devops/setup/deploy/button_x2.png" alt="Deploy to IBM Cloud">
    </a>
</p>

Click **Deploy to IBM Cloud** to deploy this same application to IBM Cloud. This option creates a deployment pipeline, complete with a hosted GitLab project and a DevOps toolchain. You can deploy your app to Cloud Foundry, a Kubernetes cluster, or a Red Hat OpenShift cluster. OpenShift is available only through a standard cluster, which requires you to have a billable account.

[IBM Cloud DevOps](https://www.ibm.com/cloud/devops) services provides toolchains as a set of tool integrations that support development, deployment, and operations tasks inside IBM Cloud.

### Building locally

To get started building this application locally, you can either run the application natively or use the [IBM Cloud Developer Tools](https://cloud.ibm.com/docs/cli?topic=cloud-cli-getting-started) for containerization and easy deployment to IBM Cloud.

#### Native application development

- Install the latest [Node.js](https://nodejs.org/en/download/) 12+ LTS version.

Once the Node toolchain has been installed and you've setup the API keys as mentioned at the start, you can download the project dependencies with:

```bash
npm install
```

To run your application locally:

```bash
npm run start
```

Your application will be running at `http://localhost:3000`.  You can access the `/health` endpoint at the host. You can also verify the state of your locally running application using the Selenium UI test script included in the `scripts` directory.

#### IBM Cloud Developer Tools

Install [IBM Cloud Developer Tools](https://cloud.ibm.com/docs/cli?topic=cloud-cli-getting-started) on your machine by running the following command:
```
curl -sL https://ibm.biz/idt-installer | bash
```

Create an application on IBM Cloud by running:

```bash
ibmcloud dev create
```

This will create and download a starter application with the necessary files needed for local development and deployment.

Your application will be compiled with Docker containers. To compile and run your app, run:

```bash
ibmcloud dev build
ibmcloud dev run
```

This will launch your application locally. When you are ready to deploy to IBM Cloud on Cloud Foundry or Kubernetes, run one of the following commands:

```bash
ibmcloud dev deploy -t buildpack // to Cloud Foundry
ibmcloud dev deploy -t container // to K8s cluster
```

You can build and debug your app locally with:

```bash
ibmcloud dev build --debug
ibmcloud dev debug
```

