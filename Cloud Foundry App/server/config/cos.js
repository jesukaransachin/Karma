var env = process.env.VCAP_SERVICES

module.exports = env['cloud-object-storage'][0].credentials;
