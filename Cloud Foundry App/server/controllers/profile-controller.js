
const CloudantService = require('../services/cloudant-service');
const config = require('../config/auth');
const axios = require('axios');
const upload = require('./upload-controller');

exports.index = (req, res) => {
  res.json({
    status: 'Up'
  });
};

exports.getProfile = (req, res) => {
  const { id } = req.body;
  CloudantService.getUserById(id)
    .then((data) => {
      res.json(data);
    }).catch((error) => {
      res.status(500).send({ error: error.error })
    });;
}

exports.updateProfile = (req, res) => {
  const { name, email, address, phone, id, age, profile, document, contactVia } = req.body;
  let profileUrl = "";
  let documentPicUrl = "";
  let shortAddress = "";
  let longAddress = "";
  let lat = "";
  let lng = "";
  let fileData = [];
  if (profile.fileName) {
    const profilePic = 'Profile/' + id + '_' + profile.fileName;
    profileUrl = config.serverUrl + '/upload/fetch/' + profilePic;
    fileData.push(profile);
  }
  if (document.fileName) {
    const documentPic = 'Document/' + id + '_' + document.fileName;
    documentPicUrl = config.serverUrl + '/upload/fetch/' + documentPic;
    fileData.push(document);

  }
  getData();

  async function getData() {
    if (address) {
      await axios.get(`https://maps.googleapis.com/maps/api/geocode/json?address=${address}&key=${config.googleApiKey}`)
        .then(function (response) {
          if (response.data.status == "OK") {
            longAddress = address;
            if (response.data.results[0].plus_code && response.data.results[0].plus_code.compound_code) {
              arr = response.data.results[0].plus_code.compound_code.split(' ');
              arr.shift();
              shortAddress = arr.join('');
            } else {
              shortAddress = response.data.results[0].formatted_address;
            }
            lat = response.data.results[0].geometry.location.lat;
            lng = response.data.results[0].geometry.location.lng;
          } else {
            lat = "0.00";
            lng = "0.00";
          }
        })
        .catch((error) => {
          res.status(500).send({ error: error.error })
        });
    }
    if (fileData) {
      for (let file of fileData) {
        let checkFile = upload.fileUpload(file, id);
      }
    }
    await CloudantService.getUserById(id)
      .then((data) => {
        const views = data.views ? data.views : 0;
        const karmapoints = data.karmapoints ? data.karmapoints : 0;
        const helpasked = data.helpasked ? data.helpasked : 0;
        const helpextended = data.helpextended ? data.helpextended : 0;
        const helpclosed = data.helpclosed ? data.helpclosed : 0;
        //if profile pic & document pic is not updated take the already uploaded picture
        profileUrl = !profile && data.profileUrl ? data.profileUrl : profileUrl;
        documentPicUrl = !document && data.documentPicUrl ? data.documentPicUrl : documentPicUrl;
        if (data) {
          CloudantService.updateUserDoc(data._id, data._rev, {
            ...data,
            name,
            email,
            longAddress,
            shortAddress,
            lat,
            lng,
            phone,
            age,
            profileUrl,
            documentPicUrl,
            views,
            karmapoints,
            helpasked,
            helpextended,
            helpclosed,
            contactVia
          })
            .then((result) => {
              res.json(data);
            }).catch((error) => {
              res.status(500).send({ error: error.error })
            });
        } else {
          CloudantService.createUserDoc({ name, email, address, phone })
            .then((data) => {
              res.json({ id: data.id });
            }).catch((error) => {
              res.status(500).send({ error: error.error })
            });
        }
      })
  }

}

exports.demoAuth = (req, res) => {
  res.status(200).json({
    message: 'Works!'
  });
};


exports.getNotifications = (req, res) => {
  const { userId } = req.body;

  CloudantService.getNotificationByUserId(userId).then((data) => {
    getData(data);
  });

  async function getData(data) {
    for (let notification of data.docs[0].notifications) {
      // console.log(notification);
      await CloudantService.getCategoryById(notification.catId).then((category) => {
        // console.log(category);
        notification.category = category.docs[0];
      }).catch((error) => {
        res.status(500).send({ error: error.error })
      });
      // console.log(notification);
    }
    // console.log(data.docs[0].notifications);
    res.json({ data: data.docs[0].notifications });
  }
}
