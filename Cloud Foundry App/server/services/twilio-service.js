const twilioConfig = require('../config/twilio');

const accountSid = twilioConfig.accountSid;
const authToken = twilioConfig.authToken;

const client = require('twilio')(accountSid, authToken);

exports.sendMessage = (message, phonenumber) => {
  return client.messages.create({
    body: message,
    from: twilioConfig.phone,
    to: phonenumber
  });
};
