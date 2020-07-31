const CosService = require('../services/cos-service');
const CloudantService = require('../services/cloudant-service');

exports.index = (req, res) => {
  res.json({
    status: 'Up'
  });
};

exports.binaryUpload = (req, res) => {
  const fileName = req.headers['x-file-name'];
  const id = req.headers['x-id'];
  const type = req.headers['x-type'];

  const final = `${type}/${id}_${fileName}`;
  const contentType = req.headers['Content-Type'];
  const contentBuffer = [];
  if (fileName) {
    req.on('data', (c) => {
      contentBuffer.push(c);
    });
    req.on('end', () => {
      var body = Buffer.concat(contentBuffer);
      CosService.uploadFile({
        key: final,
        body: body,
        ContentType: contentType
      })
        .then((result) =>
          res.status(200).json({
            message: 'Success!',
            result: result
          })
        )
        .catch((error) => {
          res.status(400).json({
            error: error
          });
        });
    });
  } else {
    res.status(400).json({
      error: 'Please specify the file name and body'
    });
  }
};

async function fileUpload(file, id) {
  const type = file.type;
  const name = file.fileName;
  const content = file.content;
  let buff = new Buffer.from(content, 'base64');
  const fileName = type + '/' + id + '_' + name;
  const contentType = 'application/octet';
  const contentBuffer = [];
  if (fileName) {
    await CosService.uploadFile({
      key: fileName,
      body: buff,
      ContentType: contentType
    })
      .then((result) => {
        return 1;
      }
      )
      .catch((error) => {

        return 0
      });
  } else {
    res.status(400).json({
      error: 'Please specify the file name and body'
    });
  }
}



exports.fileUpload = fileUpload;

exports.delete = (req, res) => {
  const { name, id, type } = req.body;
  const key = `${type}/${id}_${name}`;
  if (key) {
    CosService.deleteFile({ key })
      .then((result) => {
        res.status(200).json({
          message: 'Success!'
        });
      })
      .catch((error) => {
        res.status(500).send({ error: error.error });
      });
  } else {
    res.status(400).json({
      error: 'Please specify a filename to delete'
    });
  }
};

exports.fetch = (req, res) => {
  const { name, folder } = req.params;
  if (folder) {
    const object = folder + '/' + name;
    CosService.fetchFile({ object })
      .then((data) => {

        res.set({
          'Cache-Control': 'no-cache',
          'Content-Type': data.ContentType,
          'Content-Length': data.ContentLength,
          'Content-Disposition': 'attachment; filename=' + name
        });
        res.send(data.Body);
      })
      .catch((error) => {
        res.status(500).send({ error: error });
      });
  } else {
    res.status(400).json({
      error: 'Please specify a file name'
    });
  }
};
