import React, { Component } from 'react';
import { FormattedMessage } from 'react-intl';
import { CompactPicker } from 'react-color';
import PropTypes from 'prop-types';
import './ColorPicker.less';

const propTypes = {
  value: PropTypes.string,
  onChange: PropTypes.func, 
};
class ColorPicker extends Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
      color: props.value || 'GRAY',
    };
  }


  static getDerivedStateFromProps(nextProps) {
    if ('value' in nextProps) {
      return {
        color: nextProps.value,   
      };
    }
    return null;
  }

  componentDidMount() {
    document.addEventListener('click', this.hiddenPicker);
  }

  componentWillUnmount() {
    document.removeEventListener('click', this.hiddenPicker);
  }

  handleColorChange = (Color) => {
    const {
      r, g, b, a,
    } = Color.rgb;
    const color = `rgba(${r},${g},${b},${a})`;    
    if (!('value' in this.props)) {
      this.setState({ color });
    }
    this.triggerChange(color);
  }

  handleVisibleChange = (visible) => {       
    this.setState({ visible });   
  }

  hiddenPicker = () => {    
    this.handleVisibleChange(false);
  }

  triggerChange = (changedValue) => {
    const { onChange } = this.props;
    if (onChange) {
      onChange(changedValue);
    }
  }

  render() {
    const { visible, color } = this.state;
    return (
      <div
        role="none"
        className="c7ntest-CreateStatus-color-picker-container"
        onClick={(e) => {
          e.nativeEvent.stopImmediatePropagation();
          e.stopPropagation(); 
        }}
      >
        <FormattedMessage id="color" />
        {'：'}
        <div
          className="c7ntest-CreateStatus-color-picker-show"
          role="none"
          onClick={(e) => {
            e.nativeEvent.stopImmediatePropagation();
            e.stopPropagation();
            this.handleVisibleChange(true);
          }}
        >
          <div style={{ background: color }}>
            <div className="c7ntest-CreateStatus-color-picker-show-rec-con">
              <div className="c7ntest-CreateStatus-color-picker-show-rec" />
            </div>
          </div>
        </div>
        <div
          style={visible
            ? {
              display: 'block', position: 'absolute', bottom: 20, left: 60,
            }
            : { display: 'none' }}
        >
          <CompactPicker
            color={color}
            onChangeComplete={this.handleColorChange}
          />
        </div>
      </div>
    );
  }
}

ColorPicker.propTypes = propTypes;

export default ColorPicker;
