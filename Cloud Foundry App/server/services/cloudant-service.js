const cloudantConfig = require('../config/cloudant');

const client = require('@cloudant/cloudant')(cloudantConfig.url);
const usersdb = client.db.use('helpme');
const notificationdb = client.db.use('notifications');
const mapsdb = client.db.use('maps');
const errordb = client.db.use('errorlog');
const categoriesDb = client.db.use('categories');

exports.getPinsByPinId = (params) => {
  return mapsdb.get(params);
};

exports.createMapsDoc = (userid) => {
  const doc = {
    userid
  };
  return mapsdb.insert(doc);
};

exports.updateMapsDoc = (id, rev, params) => {
  const doc = {
    _id: id,
    _rev: rev,
    ...params
  };
  console.log(doc);
  return mapsdb.insert(doc);
};

exports.getPinsByCategory = (params) => {
  let query;
  if (!params) {
    query = {
      selector: {}
    };
  } else {
    query = {
      selector: {
        categoryId: {
          $or: params
        }
      },
      use_index: '_design/795a3d40adfb5fe99648f29e127c5f4a3468c0ac'
    };
  }
  return mapsdb.find(query);
};

exports.getPinsByCategoryAndSearch = (categories, search) => {
  const query = {
    selector: {
      categoryId: {
        $or: categories
      },
      $or: [
        {
          helptype: {
            $regex: `.*(?i)${search}.*`
          }
        },
        {
          status: {
            $regex: `.*(?i)${search}.*`
          }
        },
        {
          title: {
            $regex: `.*(?i)${search}.*`
          }
        },
        {
          description: {
            $regex: `.*(?i)${search}.*`
          }
        },
        {
          priority: {
            $regex: `.*(?i)${search}.*`
          }
        },
        {
          longaddress: {
            $regex: `.*(?i)${search}.*`
          }
        }
      ]
    },
    use_index: '_design/eaa971c672deb0378e3cde69fd299a3b301dbb2d'
  };
  return mapsdb.find(query);
};

exports.getPinsBySearch = (params) => {
  const query = {
    selector: {
      $or: [
        {
          helptype: {
            $regex: `.*(?i)${params}.*`
          }
        },
        {
          status: {
            $regex: `.*(?i)${params}.*`
          }
        },
        {
          title: {
            $regex: `.*(?i)${params}.*`
          }
        },
        {
          description: {
            $regex: `.*(?i)${params}.*`
          }
        },
        {
          priority: {
            $regex: `.*(?i)${params}.*`
          }
        },
        {
          longaddress: {
            $regex: `.*(?i)${params}.*`
          }
        }
      ]
    },
    use_index: '_design/eaa971c672deb0378e3cde69fd299a3b301dbb2d'
  };
  return mapsdb.find(query);
};

exports.getUserPin = (params) => {
  const query = {
    selector: { userid: params }
  };
  return mapsdb.find(query);
};

exports.getCategories = (params) => {
  return categoriesDb.list({ include_docs: true });
};

exports.getCategoryById = (params) => {
  const query = {
    selector: { categoryId: params }
  };
  return categoriesDb.find(query);
};

exports.createUserDoc = (params) => {
  const doc = {
    ...params
  };
  return usersdb.insert(doc);
};

exports.createUserNotify = (userId) => {
  const notifications = [];
  const doc = {
    userId,
    notifications
  };
  return notificationdb.insert(doc);
};

exports.findUser = (params) => {
  const query = {
    selector: { phone: params }
  };
  return usersdb.find(query);
};

exports.getUserById = (params) => {
  return usersdb.get(params);
};

exports.updateUserDoc = (id, rev, params) => {
  const doc = {
    _id: id,
    _rev: rev,
    ...params
  };
  return usersdb.insert(doc);
};

exports.getAllUsers = (params) => {
  return usersdb.list({ include_docs: true });
};

exports.createErrorDoc = (params) => {
  const doc = {
    ...params
  };
  return errordb.insert(doc);
};

exports.getHeroes = () => {
  const query = {
    selector: {
      karmapoints: {
        $gt: null
      }
    },
    sort: [
      {
        karmapoints: 'desc'
      }
    ],
    limit: 5
  };
  return usersdb.find(query);
};

exports.getNotificationByUserId = (params) => {
  const query = {
    selector: { userId: params }
  };
  return notificationdb.find(query);
};

exports.updateNotificationDoc = (id, rev, params) => {
  const doc = {
    _id: id,
    _rev: rev,
    ...params
  };
  return notificationdb.insert(doc);
};
