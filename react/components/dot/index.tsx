import React from 'react';
import styles from './index.less';

export interface Props extends React.HTMLAttributes<HTMLDivElement> {
  color: string
}
const Dot: React.FC<Props> = ({
  children, className, color, ...props
}) => (
  <div
    className={`${styles.dot_container} ${className || ''}`}
    {...props}
  >
    <div
      className={styles.dot}
      style={{
        background: color,
      }}
    />
    {children}
  </div>
);
export default Dot;
