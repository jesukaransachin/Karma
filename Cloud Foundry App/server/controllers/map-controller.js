// get health of application

const CloudantService = require('../services/cloudant-service');
const commonController = require('./common-controller');
const upload = require('./upload-controller');
const config = require('../config/auth');
const axios = require('axios');

exports.getCategoryList = (req, res) => {
  CloudantService.getCategories().then((data) => {
    let categories = [];
    data.rows.forEach((category) => {
      if (!category.doc.language)
        categories.push(category.doc);
    })
    res.json(categories);

  }).catch((error) => {
    res.status(500).send({ error: error.error })
  });
}

exports.home = (req, res) => {

  const { lat, lng, categoryId, search } = req.body;

  const date = new Date();
  if (!search) {
    CloudantService.getPinsByCategory(categoryId)
      .then((data) => {
        getData(data);
      }).catch((error) => {
        res.status(500).send({ error: error.error })
      });
  } else if (!categoryId && search) {
    CloudantService.getPinsBySearch(search)
      .then((data) => {
        getData(data);
      }).catch((error) => {
        res.status(500).send({ error: error.error })
      });
  } else {
    CloudantService.getPinsByCategoryAndSearch(categoryId, search)
      .then((data) => {
        getData(data);
      }).catch((error) => {
        res.status(500).send({ error: error.error })
      });
  }

  async function getData(data) {
    let result = [];
    let categories = [];
    let finalData = data.docs;
    for (let element of finalData) {
      // data.rows.forEach((element) => {
      const userLoc = {
        lat,
        lng
      };
      const pinLoc = {
        lat: element.latitude,
        lng: element.longitude
      }

      const diff = haversine_distance(userLoc, pinLoc);
      await CloudantService.getUserById(element.userid).then((userData) => {
        element.UserInfo = userData;
      }).catch((error) => {
        res.status(500).send({ error: error.error })
      });
      await CloudantService.getCategoryById(element.categoryId).then((category) => {
        element.category = category.docs[0];
      }).catch((error) => {
        res.status(500).send({ error: error.error })
      });
      element.distance = diff.toFixed(2);
      // });
      result.push(element);
    }
    await CloudantService.getCategories().then((data) => {
      data.rows.forEach((category) => {
        if (!category.doc.language)
          categories.push(category.doc);
      })
    }).catch((error) => {
      res.status(500).send({ error: error.error })
    });

    result.sort(function (a, b) {
      return a.distance - b.distance;
    });
    const finalResult = {
      result,
      categories
    }
    res.json(finalResult);

  }

  function haversine_distance(mk1, mk2) {
    const R = 3958.8; // Radius of the Earth in miles
    const rlat1 = mk1.lat * (Math.PI / 180); // Convert degrees to radians
    const rlat2 = mk2.lat * (Math.PI / 180); // Convert degrees to radians
    const difflat = rlat2 - rlat1; // Radian difference (latitudes)
    const difflon = (mk2.lng - mk1.lng) * (Math.PI / 180); // Radian difference (longitudes)

    const d = 2 * R * Math.asin(Math.sqrt(Math.sin(difflat / 2) * Math.sin(difflat / 2) + Math.cos(rlat1) * Math.cos(rlat2) * Math.sin(difflon / 2) * Math.sin(difflon / 2)));
    const final = d * 1609.34; //return meters
    return final;
  }

};

exports.myPins = (req, res) => {
  const { userId } = req.body;
  CloudantService.getUserPin(userId).then((data) => {
    getData(data);
  })


  async function getData(data) {
    let result = [];
    let categories = [];
    for (let element of data.docs) {
      // data.rows.forEach((element) => {
      for (let activity of element.activity) {
        if (activity.uid) {
          await CloudantService.getUserById(activity.uid).then((userData) => {
            activity.userData = userData;
          }).catch((error) => {
            res.status(500).send({ error: error.error })
          });
        }
      }


      await CloudantService.getCategoryById(element.categoryId).then((category) => {
        element.category = category.docs[0];
      }).catch((error) => {
        res.status(500).send({ error: error.error })
      });
      // });
      result.push(element);
    }
    const finalResult = {
      data: result
    }
    res.json(finalResult);

  }
}

exports.updatePinStatus = (req, res) => {
  const { pinId, status, userId, dateField } = req.body;

  CloudantService.getPinsByPinId(pinId).then((data) => {
    data.status = status;
    if (userId) {
      data.activity.filter((p) => p.uid == userId).map((element) => {
        element.status = status;
      });
    }

    postData(data);
  }).catch((error) => {
    res.status(500).send({ error: error.error })
  });
  async function postData(data) {
    let name;
    let userObject;
    let activityUserObject;
    await CloudantService.getUserById(data.userid)
      .then((userData) => {
        if (status === 2) {
          userData.helpclosed = (userData.helpclosed ? userData.helpclosed : 0) + 1;
          userObject = userData;
          if (data.helptype === 'Give Help') {
            userData.karmapoints = userData.karmapoints + 30;
          }
        }
        name = userData.name ? userData.name : userData.phone;
      });

    //give help -> Your request to help #name has been approved for #title
    //get help -> Your request for #title has been approved by #name 
    if (status === 1) {
      const msg = data.helptype === 'Need Help' ? `Your request to help ${name} for ${data.title} has been approved` : `${name} needs your help for "${data.title}"`;
      let check = await commonController.pushNotification(msg, pinId, userId, data.categoryId, data.helptype, dateField, status);
    } else if (status === 2) {
      const msg = `${name} has closed the help "${data.title}"`;
      let check;
      for (let activity of data.activity) {
        check = await commonController.pushNotification(msg, pinId, activity.uid, data.categoryId, data.helptype, dateField, status);
        if (data.helptype === 'Need Help' && activity.status === 1) {
          await CloudantService.getUserById(activity.uid).then((activityUserData) => {
            activityUserData.karmapoints = activityUserData.karmapoints + 30;
            activityUserObject = activityUserData;
          }).catch((error) => {
            res.status(500).send({ error: error.error })
          });
          await CloudantService.updateUserDoc(activityUserObject.id, activityUserObject.rev, activityUserObject);
        }
      }

      await CloudantService.updateUserDoc(userObject.id, userObject.rev, userObject);
    }


    CloudantService.updateMapsDoc(data._id, data._rev, data).then((updateData) => {
      res.json({ result: 'Success' })
    }).catch((error) => {
      res.status(500).send({ error: error.error })
    });
  }
}
exports.createPins = (req, res) => {
  const { userid, helptype, title, description, files, categoryId, priority, videoName } = req.body;
  getData();
  let longaddress, latitude, longitude;
  let activity = [];
  let photos = [];
  const views = 0;
  const status = 0;
  let userObject;
  let id, rev;
  async function getData() {

    await CloudantService.createMapsDoc(userid).then((data) => {
      id = data.id;
      rev = data.rev;
    });
    await CloudantService.getUserById(userid).then((userData) => {
      longaddress = userData.shortAddress;
      latitude = userData.lat;
      longitude = userData.lng;
      userData.helpasked = (userData.helpasked ? userData.helpasked : 0) + 1;
      userObject = userData;
    }).catch((error) => {
      res.status(500).send({ error: error.error })
    });
    if (files) {
      for (let file of files) {
        let checkFile = await upload.fileUpload(file, id);
        let fileName = file.type + '/' + id + '_' + file.fileName;
        let profileUrl = config.serverUrl + '/upload/fetch/' + fileName;
        photos.push(profileUrl);
      }
    }
    if (videoName) {
      let videoFile = 'Maps' + '/' + id + '_' + videoName;
      let videoUrl = config.serverUrl + '/upload/fetch/' + videoFile;
      photos.push(videoUrl);
    }
    await CloudantService.updateUserDoc(userObject.id, userObject.rev, userObject);

    await CloudantService.updateMapsDoc(id, rev,
      {
        userid, helptype, title, description, photos, activity, categoryId, priority, status, views, latitude, longitude, longaddress
      }).then((updateData) => {
        res.json({ id })
      }).catch((error) => {
        res.status(500).send({ error: error.error })
      });
  }

}

exports.readyToHelp = (req, res) => {
  const { pinId, userId, dateField } = req.body;

  CloudantService.getPinsByPinId(pinId).then((data) => {
    const entry = {
      "uid": userId,
      "status": 0
    }
    data.activity.push(entry);
    postData(data);
  }).catch((error) => {
    res.status(500).send({ error: error.error })
  });
  async function postData(data) {
    let name;
    let userObject;
    await CloudantService.getUserById(userId)
      .then((userData) => {
        userData.helpextended = (userData.helpextended ? userData.helpextended : 0) + 1;
        name = userData.name ? userData.name : userData.phone;
        userObject = userData;
      });

    const msg = data.helptype === 'Need Help' ? `${name} wants to help you for "${data.title}"` : `${name} needs your help for "${data.title}"`;

    let check = await commonController.pushNotification(msg, pinId, data.userid, data.categoryId, data.helptype, dateField, 0);
    await CloudantService.updateUserDoc(userObject.id, userObject.rev, userObject);

    await CloudantService.updateMapsDoc(data._id, data._rev, data).then((updateData) => {
      res.json({ result: 'Success' })
    }).catch((error) => {
      res.status(500).send({ error: error.error })
    });
  }

}

exports.getHeroes = (req, res) => {
  let helpGiven = 0;
  let helpRequested = 0;
  let usersBenefitted = 0;
  let heroes;
  getData();
  async function getData() {
    await CloudantService.getAllUsers().then((data) => {
      for (let element of data.rows) {
        helpRequested += element.doc.helpasked ? element.doc.helpasked : 0;
        usersBenefitted += element.doc.helpclosed ? element.doc.helpclosed : 0;
        helpGiven += element.doc.helpextended ? element.doc.helpextended : 0;
      }
    }).catch((error) => {
      res.status(500).send({ error: error.error })
    });;

    await CloudantService.getHeroes().then((data) => {
      heroes = data.docs;
    }).catch((error) => {
      res.status(500).send({ error: error.error })
    });
    res.json({
      helpRequested,
      helpGiven,
      usersBenefitted,
      heroes
    })
  }

}