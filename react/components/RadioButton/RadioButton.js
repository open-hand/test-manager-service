import React, { Component } from 'react';
import { Radio } from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import './RadioButton.less';

class RadioButton extends Component {
  render() {
    const { style, data } = this.props;
    return (
      <div className="c7ntest-radio-button" style={style}>
        <Radio.Group {...this.props} style={null}>
          {
            data.map(button => (
              <Radio.Button value={button.value} key={button.value}>
                <FormattedMessage id={button.text} />
              </Radio.Button>
            ))
          }        
        </Radio.Group>
      </div>
    );
  }
}

RadioButton.propTypes = {

};

export default RadioButton;
