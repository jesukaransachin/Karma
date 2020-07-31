/* eslint-disable comma-dangle */
// import dependencies and initialize the express router
const express = require('express');
const AuthController = require('../controllers/auth-controller');

const AuthMiddleware = require('../middleware/jwt-middleware');

const router = express.Router();

// define routes
router.get('', AuthController.index);
router.post('/login',
  AuthController.login);
router.post('/loadNotify',
  AuthController.loadNotify);
router.post('/verified',
  AuthController.verified);
router.post(
  '/refresh',
  AuthMiddleware.authenticateRefreshToken,
  AuthController.refresh
);
router.get(
  '/demoauth',
  AuthMiddleware.authenticateToken,
  AuthController.demoAuth
);

module.exports = router;
