import React from "react";
import { List } from 'react-virtualized';

export const Lists = ({lists}) => {

  return (
    <div>
      {Object.keys(lists).map((listName, index1) => (
        <div>
          <i class="fas fa-list"></i> <b>{listName}</b> ({lists[listName].length})
          <div style={{paddingLeft: "20px"}}>
            {lists[listName].length<10 &&
              <div style={{width:"400px"}}>
                {lists[listName].map((item, index2) => (
                  <div>
                  {item}<br />
                  </div>
                ))}
              </div>
            }
            {lists[listName].length>=10 &&
              <div style={{height:"200px", width:"400px", overflowY:"scroll"}}>
              {lists[listName].map((item, index2) => (
                <div>
                {item}<br />
                </div>
              ))}
              </div>
            }
          </div>
          <br /><br />
        </div>
      ))}
    </div>
  );
};
