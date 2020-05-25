const alias = require('./alias').gulp;
module.exports = {
  presets: [
    '@babel/preset-react',
    ['@babel/preset-env', {
      modules: false,
    }],
  ],
  plugins: [
    '@umijs/babel-plugin-auto-css-modules',
    [
      'babel-plugin-module-resolver',
      {
        alias,
      }
    ],
    '@babel/plugin-proposal-export-default-from',
    '@babel/plugin-proposal-export-namespace-from',
    ['@babel/plugin-proposal-decorators', {
      legacy: true,
    }],
    ['@babel/plugin-proposal-class-properties', {
      loose: true,
    }],
    ['babel-plugin-import',
      {
        libraryName: 'choerodon-ui',
        style: true,
      },
      'choerodon-ui',
    ], ['babel-plugin-import',
      {
        libraryName: 'choerodon-ui/pro',
        style: true,
      },
      'choerodon-ui-pro',
    ],
    'babel-plugin-lodash',
    ['babel-plugin-try-import', {
      tryImport: 'C7NTryImport',
      hasModule: 'C7NHasModule',
    }],
  ],
}