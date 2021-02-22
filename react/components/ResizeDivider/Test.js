import React, { Component } from 'react';
import ResizeContainer from './ResizeContainer';

const { Section, Divider } = ResizeContainer;
class Test extends Component {
  saveRef = name => (ref) => {
    this[name] = ref;
  }

  render() {
    return (
      <div style={{ width: 500, height: 500 }}>
        <ResizeContainer type="horizontal">
          <Section size={{
            width: 100,
            minWidth: 50,
          }}
          >
            <div style={{ flex: 1, background: 'aqua', height: '100%' }}>
              left
            </div>
          </Section>
          <Divider />
          <Section size={{
            width: 120,
            // minWidth: 50,
          }}
          >
            <div style={{ flex: 1, background: 'bisque', height: '100%' }}>
              middle
            </div>
          </Section>
          <Divider />
          <Section size={{
            width: 100,
            maxWidth: 120,
            minWidth: 50,
          }}
          >
            <div style={{ flex: 1, background: 'blueviolet', height: '100%' }}>
              right
            </div>
          </Section>
        </ResizeContainer>
        <ResizeContainer type="vertical">
          <Section size={{
            // width: 100,
            minHeight: 50,
            height: 100,
          }}
          >
            <div style={{ flex: 1, background: 'aqua', height: '100%' }}>
              top
            </div>
          </Section>
          <Divider />
          <Section size={{
            height: 100,
            minHeight: 60,
          }}
          >
            <div style={{ flex: 1, background: 'bisque', height: '100%' }}>
              middle
            </div>
          </Section>
          <Divider />
          <Section size={{
            height: 100,
          }}
          >
            <div style={{ flex: 1, background: 'blueviolet', height: '100%' }}>
              bottom
            </div>
          </Section>
        </ResizeContainer>
      </div>
    );
  }
}

Test.propTypes = {

};

export default Test;
