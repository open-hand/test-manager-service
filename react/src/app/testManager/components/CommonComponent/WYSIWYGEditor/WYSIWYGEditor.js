/* eslint-disable */
import React, { Component } from 'react';
import ReactQuill, { Quill } from 'react-quill';
import { Button } from 'choerodon-ui';
import 'react-quill/dist/quill.snow.css';
import ImageDrop from './ImageDrop';
import Link from './Link';
import './WYSIWYGEditor.scss';

Quill.register('modules/imageDrop', ImageDrop);
Quill.register('formats/link', Link);

class WYSIWYGEditor extends Component {
  state = {
    value: null,
  }

  modules = {
    toolbar: [
      ['bold', 'italic', 'underline', 'strike', 'blockquote'],
      [{ list: 'ordered' }, { list: 'bullet' }, 'image', 'link', { color: [] }],
    ],
    imageDrop: true,
  };
  
  formats = [
    'bold',
    'italic',
    'underline',
    'strike',
    'blockquote',
    'list',
    'bullet',
    'link',
    'image',
    'color',
  ];

  defaultStyle = {
    width: 498,
    height: 200,
    borderRight: 'none',
  };

  handleChange = (content, delta, source, editor) => {   
    const value = editor.getContents();
    this.setState({
      value: value.ops,
    });
    if (this.props.onChange && value && value.ops) {
      this.props.onChange(value.ops);
    }
  };

  // componentWillReceiveProps(nextProps) {       
  //   if (this.props.value !== nextProps.value) {
  //     this.editor.setEditorContents(this.editor.getEditor(), nextProps.value);
  //   }  
  // }

  render() {
    const { placeholder, value } = this.props;
    let defaultValue = value;
    try {
      defaultValue = JSON.parse(value);
    } catch (error) {
      defaultValue = value;
    }
    const style = { ...this.defaultStyle, ...this.props.style };
    const editHeight = style.height - (this.props.toolbarHeight || 42);
    return (
      <div style={{ width: '100%' }}>
        <div style={style} className="react-quill-editor" ref={container => this.container = container}>
          <ReactQuill
            ref={(editor) => { this.editor = editor; }}
            theme="snow"
            modules={this.modules}
            formats={this.formats}
            style={{ height: editHeight }}
            placeholder={placeholder || Choerodon.getMessage('描述', 'Description')}            
            defaultValue={defaultValue}
            onChange={this.handleChange}
            bounds={this.container}
          />
        </div>
        {
          this.props.bottomBar && (
            <div style={{
              padding: '0 8px',
              border: '1px solid #ccc',
              borderTop: 'none',
              display: 'flex',
              justifyContent: 'flex-end',
              height: 35,
            }}
            >
              <Button
                type="primary"
                onClick={() => this.props.handleDelete && this.props.handleDelete()}
              >
                {Choerodon.getMessage('取消', 'Cancle')}
              </Button>
              <Button
                type="primary"
                onClick={() => this.props.handleSave(this.state.value)}
              >
                {Choerodon.getMessage('保存', 'Save')}
              </Button>
            </div>
          )
        }
      </div>
    );
  }
}

export default WYSIWYGEditor;
