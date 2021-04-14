import React, { useState } from 'react';
import Case from './components/case';

export interface LinkedTestCase {

}
const IssueLinkedTestCase = () => {
  const [data, setData] = useState([]);
  return (
    <div>
      ll
      {data.map((item) => <Case data={item} />)}
    </div>
  );
};

export default IssueLinkedTestCase;
