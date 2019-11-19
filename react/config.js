/* eslint-disable */
const alias = require('../alias').webpack;

const config = {
  master: './node_modules/@choerodon/master/lib/master.js',
  projectType: 'choerodon',
  buildType: 'single',
  dashboard: {},
  webpackConfig(configs) {
    configs.resolve.alias = alias;    
    return configs;
  },
  modules: [
    '.',
  ],
};

module.exports = config;
