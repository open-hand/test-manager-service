import React from 'react';
import styles from './index.less';

export interface Props extends React.HTMLAttributes<HTMLDivElement> {
}
const Card: React.FC<Props> = ({ children, className, ...props }) => (
  <div className={`${styles.card} ${className || ''}`} {...props}>
    {children}
  </div>
);
export default Card;
