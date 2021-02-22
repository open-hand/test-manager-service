/* eslint-disable react/no-find-dom-node */
import React, { Component } from 'react';
import { findDOMNode } from 'react-dom';
import PropTypes from 'prop-types';
import './Divider.less';

class Divider extends Component {
  state = {
    resizing: false,
  };

  static defaultProps = {
    type: 'horizontal',
  };

  getBindElements = () => {
    const { parent, bindings } = this.props;
    const preElement = parent[bindings[0]];
    const nextElement = parent[bindings[1]];
    return {
      preElement: preElement && findDOMNode(preElement),
      nextElement: nextElement && findDOMNode(nextElement),
    };
  };

  getBindProps = () => {
    const { parent, bindings } = this.props;
    const preElement = parent[bindings[0]];
    const nextElement = parent[bindings[1]];
    return {
      preProps: preElement ? preElement.props : { size: {} },
      nextProps: nextElement ? nextElement.props : { size: {} },
    };
  }

  getResizeWidth = (vary, size, originSize) => {
    const { width: initWidth, height: initHeight } = originSize;
    const {
      height, width, minHeight, minWidth, maxHeight, maxWidth,
    } = size;
    let Width = 0;

    if (maxWidth !== undefined && initWidth + vary > maxWidth) {
      Width = maxWidth;
    } else if (minWidth !== undefined && initWidth + vary < minWidth) {
      Width = minWidth;
    } else {
      Width = initWidth + vary;
    }
    return Math.max(Width, 0);
  }

  getResizeHeight = (vary, size, originSize) => {
    const { width: initWidth, height: initHeight } = originSize;
    const {
      height, width, minHeight, minWidth, maxHeight, maxWidth,
    } = size;
    let Height = 0;

    if (maxHeight !== undefined && initHeight + vary > maxHeight) {
      Height = maxHeight;
    } else if (minHeight !== undefined && initHeight + vary < minHeight) {
      Height = minHeight;
    } else {
      Height = initHeight + vary;
    }
    return Math.max(Height, 0);
  }

  handleMouseDown = (e) => {
    e.stopPropagation();
    e.preventDefault();
    this.setState({
      resizing: true,
    });
    const { preElement, nextElement } = this.getBindElements();
    // 设置默认值
    this.originPosition = {
      x: e.clientX,
      y: e.clientY,
      prePosition: {
        width: preElement ? preElement.offsetWidth : 0,
        height: preElement ? preElement.offsetHeight : 0,
      },
      nextPosition: {
        width: nextElement ? nextElement.offsetWidth : 0,
        height: nextElement ? nextElement.offsetHeight : 0,
      },
    };
    document.addEventListener('mouseup', this.handleMouseUp);
    document.addEventListener('mousemove', this.handleMouseMove);
  }

  handleMouseUp = (e) => {
    document.removeEventListener('mousemove', this.handleMouseMove);
    document.removeEventListener('mouseup', this.handleMouseUp);
    this.originPosition = {
      x: e.clientX,
      y: e.clientY,
    };
    this.setState({
      resizing: false,
    });
  }

  handleMouseMove = (e) => {
    e.stopPropagation();
    e.preventDefault();
    const {
      x, y, prePosition, nextPosition,
    } = this.originPosition;
    const differX = e.clientX - x;
    const differY = e.clientY - y;
    const { onResize } = this.props;
    const { preElement, nextElement } = this.getBindElements();
    const { preProps, nextProps } = this.getBindProps();
    if (this.props.type === 'vertical') {
      const preHeight = this.getResizeHeight(differY, preProps.size, prePosition);
      const nextHeight = this.getResizeHeight(-differY, nextProps.size, nextPosition);
      const totalHeight = prePosition.height + nextPosition.height;
      // 高度没有溢出再设置宽度
      if (preHeight + nextHeight === totalHeight) {
        if (preElement) {
          preElement.style.height = `${preHeight}px`;
        }
        if (nextElement) {
          nextElement.style.height = `${nextHeight}px`;
        }
        if (onResize) {
          onResize([preHeight, nextHeight]);
        }
      }
    } else {
      const preWidth = this.getResizeWidth(differX, preProps.size, prePosition);
      const nextWidth = this.getResizeWidth(-differX, nextProps.size, nextPosition);
      const totalWidth = prePosition.width + nextPosition.width;
      // 宽度没有溢出再设置宽度
      if (preWidth + nextWidth === totalWidth) {
        if (preElement) {
          preElement.style.width = `${preWidth}px`;
        }
        if (nextElement) {
          nextElement.style.width = `${nextWidth}px`;
        }
        if (onResize) {
          onResize([preWidth, nextWidth]);
        }
      }
    }
  }

  render() {
    const { type } = this.props;
    const { resizing } = this.state;
    return (
      <div className={`Divider ${type}`} onMouseDown={this.handleMouseDown} role="none">
        <hr className="divider-line" />
        {/* 拖动时，创建一个蒙层来显示拖动效果，防止鼠标指针闪烁 */}
        {resizing && (
          <div style={{
            position: 'fixed',
            top: 0,
            left: 0,
            bottom: 0,
            right: 0,
            zIndex: 9999,
            cursor: type === 'vertical' ? 'row-resize' : 'col-resize',
          }}
          />
        )}
      </div>
    );
  }
}

Divider.propTypes = {
  type: PropTypes.oneOf(['vertical', 'horizontal']),
};
Divider.displayName = 'Divider';

export default Divider;
