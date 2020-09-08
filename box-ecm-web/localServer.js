let express = require('express');
let app = express();

app.use(express.static('./dist'));
app.use(
  express.static('./dist/', {
    extensions: ['html']
  })
);
console.log('http://localhost:9000');
app.listen(9000);
