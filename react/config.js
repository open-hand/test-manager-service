/* eslint-disable */
const alias = require('../alias').webpack;

const config = {
  server: 'http://api.staging.saas.hand-china.com',
  master: './node_modules/@choerodon/master/lib/master.js',
  projectType: 'choerodon',
  buildType: 'single',
  dashboard: {},
  webSocketServer: 'ws://notify.staging.saas.hand-china.com',
  webpackConfig(configs) {
    configs.resolve.alias = alias;    
    return configs;
  },
  modules: [
    '.',
  ],
};

module.exports = config;
