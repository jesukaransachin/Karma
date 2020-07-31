const jwt = require('jsonwebtoken');
const jwtConfig = require('../config/jwt');

exports.createAccessToken = (params) => {
  return jwt.sign(
    params,
    jwtConfig.tokenSecret
    // { expiresIn: '1d' }
  );
};

exports.createRefreshToken = (params) => {
  return jwt.sign(params, jwtConfig.refreshtokenSecret);
};
