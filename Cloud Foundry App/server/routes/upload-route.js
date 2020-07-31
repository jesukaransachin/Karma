// import dependencies and initialize the express router
const express = require('express');
const UploadController = require('../controllers/upload-controller');
const cc = require('../controllers/common-controller');
const AuthMiddleware = require('../middleware/jwt-middleware');

const router = express.Router();

// define routes
router.get('', UploadController.index);
router.post('/uploadVideo',
    // AuthMiddleware.authenticateToken,
    UploadController.binaryUpload);
// router.post('/store',
//     // AuthMiddleware.authenticateToken,
//     UploadController.store);
router.post('/delete',
    AuthMiddleware.authenticateToken,
    UploadController.delete);

router.get('/push',
    // AuthMiddleware.authenticateToken,
    cc.pushNotificationFCM);
router.get('/fetch/:folder/:name',
    // AuthMiddleware.authenticateToken,
    UploadController.fetch);

module.exports = router;
