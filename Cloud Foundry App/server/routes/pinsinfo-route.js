// import dependencies and initialize the express router
const express = require('express');
const PinInfoController = require('../controllers/map-controller');
const AuthMiddleware = require('../middleware/jwt-middleware');

const router = express.Router();

// define routes
router.post('/home',
    AuthMiddleware.authenticateToken,
    PinInfoController.home);
router.post('/myHelps',
    AuthMiddleware.authenticateToken,
    PinInfoController.myPins);
router.post('/updatePinStatus',
    // AuthMiddleware.authenticateToken,
    PinInfoController.updatePinStatus);
router.post('/readyToHelp',
    // AuthMiddleware.authenticateToken,
    PinInfoController.readyToHelp);
router.post('/createHelp',
    AuthMiddleware.authenticateToken,
    PinInfoController.createPins);
router.post('/getHeroes',
    AuthMiddleware.authenticateToken,
    PinInfoController.getHeroes);
router.post('/getCategories',
    AuthMiddleware.authenticateToken,
    PinInfoController.getCategoryList);

module.exports = router;
