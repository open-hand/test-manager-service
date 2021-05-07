import React from 'react';

const cardStyle: React.CSSProperties = {
  background: '#F5F6FA',
  borderRadius: '8px',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  // width: '100%',
  marginTop: '20px',
  padding: '20px 16px',
};
const headerStyle: React.CSSProperties = {
  fontSize: '16px',
  fontWeight: 500,
  color: '#3A345F',
  marginBottom: '25px',
  display: 'flex',
  alignItems: 'center',
};
interface SectionProps {
  title: string
}
const Section: React.FC<SectionProps> = ({ children, title }) => (
  <div style={cardStyle}>
    <div style={{ width: '100%' }}>
      <div style={headerStyle}>
        {title}
      </div>
      {children}
    </div>
  </div>
);
export default Section;
