var env = process.env.VCAP_SERVICES
module.exports = env.cloudantNoSQLDB[0].credentials;
