const CloudantService = require('../services/cloudant-service');
var admin = require('firebase-admin');
var serviceAccount = require('../config/firebase.json');
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: `https://${serviceAccount.project_id}.firebaseio.com`
});
async function pushNotification(
  msg,
  helpid,
  userId,
  catId,
  helpType,
  dateField,
  status
) {
  const obj = {
    msg,
    helpid,
    catId,
    helpType,
    dateField
  };
  let registrationToken;
  await CloudantService.getNotificationByUserId(userId).then((data) => {
    data.docs[0].notifications.push(obj);
    CloudantService.updateNotificationDoc(
      data.docs[0]._id,
      data.docs[0]._rev,
      data.docs[0]
    );
  });

  await CloudantService.getUserById(userId)
    .then((data) => {
      registrationToken = data.regToken;
    })
    .catch((error) => {
      res.status(500).send({ error: error.error });
    });
  let title =
    helpType === 'Need Help' ? 'Hero has RISEN!' : 'Time to be a hero!';
  if (status == 2) title = 'Mission Accomplished!';
  const payload = {
    notification: {
      title: title,
      body: msg
    },
    data: {
      account: 'Savings',
      balance: '$3020.25'
    }
  };

  const options = {
    priority: 'high',
    timeToLive: 60 * 60 * 24
  };

  await admin
    .messaging()
    .sendToDevice(registrationToken, payload, options)
    .then((response) => {
      console.log(response);
    })
    .catch((error) => {
      console.log(error);
    });
  return 1;
}

exports.pushNotification = pushNotification;

exports.pushNotificationFCM = (ad) => {};
