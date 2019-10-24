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
        className="issue-color-picker"
        onClick={(e) => {
          e.nativeEvent.stopImmediatePropagation();
          e.stopPropagation(); 
        }}
      >
        <div className="issue-priority-swatch" onClick={() => this.handleVisibleChange(true)} role="none">
          <div className="issue-priority-color" style={{ background: color }} />
        </div>
        {
        visible
          ? (
            <div className="popover">
              <div className="cover" onClick={() => this.handleVisibleChange(false)} role="none" />
              <CompactPicker color={color} onChange={this.handleColorChange} />
            </div>
          )
          : null
      }
      </div>
    );
  }
}

ColorPicker.propTypes = propTypes;

export default ColorPicker;
