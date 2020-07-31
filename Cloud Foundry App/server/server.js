/* eslint-disable comma-dangle */
// import dependencies and initialize express
const express = require('express');
const winston = require('winston');
const winstonCloudant = require('winston-cloudant');
const expressWinston = require('express-winston');
const path = require('path');
const bodyParser = require('body-parser');
const session = require('express-session');
const healthRoutes = require('./routes/health-route');
const swaggerRoutes = require('./routes/swagger-route');
const authRoutes = require('./routes/authentication-route');
const uploadRoutes = require('./routes/upload-route');
const profileRoutes = require('./routes/profile-route');
const mapRoutes = require('./routes/pinsinfo-route');
const fbconfig = require('./config/auth');
const cloudantConfig = require('./config/cloudant');
const app = express();
var passport = require('passport');
const CloudantTransport = require('winston-cloudant');
var FacebookStrategy = require('passport-facebook').Strategy;
// enable parsing of http request body

app.use(
  expressWinston.logger({
    transports: [
      new CloudantTransport({
        url: cloudantConfig.url,
        db: 'logs',
        username: cloudantConfig.username,
        password: cloudantConfig.password,
        host: cloudantConfig.host,
        iamApiKey: cloudantConfig.apikey
      })
    ]
  })
);
app.use(
  expressWinston.errorLogger({
    transports: [
      new CloudantTransport({
        url: cloudantConfig.url,
        db: 'error-logs',
        username: cloudantConfig.username,
        password: cloudantConfig.password,
        host: cloudantConfig.host,
        iamApiKey: cloudantConfig.apikey
      })
    ]
  })
);

// app.use(
//   express.errorLogger({
//     dumpExceptions: true,
//     showStack: true
//   })
// );

app.use(express.json({ limit: '50mb' }));
app.use(express.urlencoded({ limit: '50mb', extended: false }));
global.date = new Date();
app.use(
  session({
    secret: 'hola',
    resave: true,
    saveUninitialized: true
  })
);
app.use(passport.initialize());
app.use(passport.session());
// routes and api calls
app.use('/health', healthRoutes);
app.use('/swagger', swaggerRoutes);
app.use('/auth', authRoutes);
app.use('/upload', uploadRoutes);
app.use('/profile', profileRoutes);
app.use('/maps', mapRoutes);

//facebook login start
app.get(
  '/auth/facebook',
  passport.authenticate('facebook', { scope: ['email'] })
);
app.get(
  '/auth/facebook/callback',
  passport.authenticate('facebook', {
    successRedirect: '/success',
    failureRedirect: '/login'
  })
);

app.get('/success', function (req, res) {
  res.json(req.user._json);
});

passport.serializeUser(function (user, done) {
  done(null, user);
});

passport.deserializeUser(function (user, done) {
  done(null, user);
});

passport.use(
  new FacebookStrategy(
    {
      clientID: fbconfig.facebookAuth.clientId,
      clientSecret: fbconfig.facebookAuth.clientSecret,
      callbackURL:
        'https://nodejs-express-app-pdest.eu-gb.mybluemix.net/auth/facebook/callback/',
      profileFields: ['id', 'email', 'picture.type(large)', 'displayName']
    },
    function (accessToken, refreshToken, profile, done) {
      process.nextTick(function () {
        done(null, profile);
      });
    }
  )
);

//facebook login end

// start node server
const port = process.env.PORT || 3000;
app.listen(port, () => {
  console.log(`App UI available http://localhost:${port}`);
  console.log(`Swagger UI available http://localhost:${port}/swagger/api-docs`);
});

// default path to serve up index.html (single page application)
app.all('', (req, res) => {
  res.status(200).sendFile(path.join(__dirname, '../public', 'index.html'));
});
// error handler for unmatched routes or api calls
app.use((req, res, next) => {
  res.status(404).sendFile(path.join(__dirname, '../public', '404.html'));
});

module.exports = app;
