const path = require('path');

module.exports = {
  mode: 'production',
  entry: './src/index.js',
  output: {
    filename: 'processors.js',
    path: path.resolve(__dirname, '../frontend/public/worker'),
  },
};
