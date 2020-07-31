const ibm = require('ibm-cos-sdk');
const util = require('util');
const cosConfig = require('../config/cos');
const creds = {
  endpoint: cosConfig.endpoint,
  apiKeyId: cosConfig.apikey,
  serviceInstanceId: cosConfig.resource_instance_id
};

const client = new ibm.S3(creds);

exports.uploadFile = (params) => {
  return client
    .putObject({
      Bucket: cosConfig.bucket_name,
      Key: params.key,
      Body: params.body
    })
    .promise();
};

exports.deleteFile = (params) => {
  return client
    .deleteObject({
      Bucket: cosConfig.bucket_name,
      Key: params.key
    })
    .promise();
};

exports.fetchFile = (params) => {
  return client
    .getObject({
      Bucket: cosConfig.bucket_name,
      Key: params.object
    })
    .promise();
};
