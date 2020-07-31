// get health of application
exports.getHealth = (req, res) => {
  console.log(req.user);
  res.json({
    status: 'UP',
  });
};
