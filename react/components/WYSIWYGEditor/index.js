let WYSIWYGEditor = null;
try {
  WYSIWYGEditor = require('@choerodon/agile-pro/lib/components/WYSIWYGEditor').default;
} catch (error) {
  WYSIWYGEditor = require('@choerodon/agile/lib/components/WYSIWYGEditor').default;
}

export default WYSIWYGEditor;
