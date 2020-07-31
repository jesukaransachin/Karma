// get health of application

const TwilioService = require('../services/twilio-service');
const CloudantService = require('../services/cloudant-service');

const JwtUtil = require('../utils/jwt-util');

exports.index = (req, res) => {
  res.json({
    status: 'Up'
  });
};

exports.updateProfile = (req, res) => {
  const { name, email, address, phone, id } = req.body;
  CloudantService.getUserById(id)
    .then((data) => {
      if (data) {
        CloudantService.updateUserDoc(data._id, data._rev, {
          name,
          email,
          address,
          phone
        })
          .then((data) => {
            res.json({ id: data.id });
          }).catch((error) => {
            res.status(500).send({ error: error.error })
          });
      } else {
        CloudantService.createUserDoc({ name, email, address, phone })
          .then((data) => {
            res.json({ id: data.id });
          })
      }
    })
}

exports.login = (req, res) => {
  const OTP = Math.floor(1000 + Math.random() * 9000);
  const { phone } = req.body;
  let userId;
  if (phone) {
    // Check if user exists
    CloudantService.findUser(phone).then((result) => {
      if (result.docs.length > 0) {
        CloudantService.updateUserDoc(result.docs[0]._id, result.docs[0]._rev, {
          ...result.docs[0],
          phone: result.docs[0].phone,
          OTP: OTP
        })
          .then((data) => {
            TwilioService.sendMessage(OTP, phone)
              .then((message) => {
                res.json({ ...data, OTP: OTP });
              })
              .catch((error) => {
                res.status(500).send({ error: error.error })
              });
          })
          .catch((error) => {
            res.status(500).send({ error: error.error })
          });
      } else {
        CloudantService.createUserDoc({ phone, OTP })
          .then((data) => {

            userId = data.id;
            TwilioService.sendMessage(OTP, phone)
              .then((message) => {
                CloudantService.createUserNotify(userId)
                  .then((result) => {
                    res.json({ ...data, OTP: OTP });
                  })
              })
              .catch((error) => {
                res.status(500).send({ error: error.error })
              });
          })
          .catch((error) => {
            res.status(500).send({ error: error.error })
          });
      }
    });
  } else {
    res.status(400).json({
      error: 'Please supply a phone number'
    });
  }
};

exports.verified = (req, res) => {
  const { id, lat, lng, regToken } = req.body;
  if (!lat && !lng) {
    lat = "0.00";
    lng = "0.00";
  }
  if (id) {
    CloudantService.getUserById(id)
      .then((data) => {
        const accessToken = JwtUtil.createAccessToken(data);
        const refreshToken = JwtUtil.createRefreshToken(data);
        // console.log(data);
        data.lat = data.lat ? data.lat : lat;
        data.lng = data.lng ? data.lng : lng;
        data.regToken = regToken;
        CloudantService.updateUserDoc(data._id, data._rev, {
          ...data
        })
          .then((result) => {
            res.status(200).json({
              ...data,
              accessToken,
              refreshToken
            });
          })
      })
      .catch((error) => {
        res.status(500).send({ error: error.error })
      });
  } else {
    res.status(400).json({
      error: 'Please provide a valid id'
    });
  }
};

exports.refresh = (req, res) => {
  const { id, token } = req.body;
  if (token && id) {
    CloudantService.getUserById(id)
      .then((data) => {
        const accessToken = JwtUtil.createAccessToken(data);
        res.status(200).json({
          id,
          accessToken
        });
      })
      .catch((error) => {
        res.status(500).send({ error: error.error })
      });
  } else {
    res.status(400).json({
      error: 'Please provide a refresh token and an id'
    });
  }
};

exports.loadNotify = (req, res) => {
  CloudantService.getAllUsers().then((data) => {
    for (let element of data.rows) {
      CloudantService.createUserNotify(element.id)
        .then((result) => {
        })
    }
  })

}

exports.demoAuth = (req, res) => {
  res.status(200).json({
    message: 'Works!'
  });
};
