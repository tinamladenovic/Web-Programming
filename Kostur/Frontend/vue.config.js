const webpack = require('webpack');

module.exports = {
  transpileDependencies: true,
  devServer: {
    port: 3000
  },
  configureWebpack: {
    plugins: [
      new webpack.DefinePlugin({
        '__VUE_PROD_HYDRATION_MISMATCH_DETAILS__': true
      })
    ]
  }
}
