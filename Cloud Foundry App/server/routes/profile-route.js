/* eslint-disable comma-dangle */
// import dependencies and initialize the express router
const express = require('express');
const ProfileController = require('../controllers/profile-controller');
const router = express.Router();
const AuthMiddleware = require('../middleware/jwt-middleware');

// define routes
router.get('',
    AuthMiddleware.authenticateToken,
    ProfileController.index);
router.post('/updateProfile',
    AuthMiddleware.authenticateToken,
    ProfileController.updateProfile);
router.post('/getProfile',
    AuthMiddleware.authenticateToken, ProfileController.getProfile);
router.post('/getNotifications',
    AuthMiddleware.authenticateToken, ProfileController.getNotifications);

module.exports = router;
