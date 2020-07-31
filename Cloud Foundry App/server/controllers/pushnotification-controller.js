

const registrationToken = req.body.registrationToken
const message = req.body.message
const options = notification_options

admin.messaging().sendToDevice(registrationToken, message, options)
    .then(response => {

        res.status(200).send("Notification sent successfully")

    })
    .catch(error => {
        console.log(error);
    });