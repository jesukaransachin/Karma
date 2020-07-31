const jwt = require('jsonwebtoken');
const jwtConfig = require('../config/jwt');

exports.authenticateToken = (req, res, next) => {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];
  if (token == null) {
    return res.sendStatus(401);
  }

  jwt.verify(token, jwtConfig.tokenSecret, (err, user) => {
    if (err) return res.sendStatus(403);
    req.user = user;
    next();
  });
};

exports.authenticateRefreshToken = (req, res, next) => {
  const token = req.body['token'];
  if (token == null) {
    return res.sendStatus(401);
  }

  jwt.verify(token, jwtConfig.refreshtokenSecret, (err, user) => {
    if (err) return res.sendStatus(403);
    req.user = user;
    next();
  });
};
