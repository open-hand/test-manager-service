let TextEditToggle = null;
try {
  TextEditToggle = require('@choerodon/agile-pro/lib/components/TextEditToggle').default;
} catch (error) {
  TextEditToggle = require('@choerodon/agile/lib/components/TextEditToggle').default;
}

export default TextEditToggle;
