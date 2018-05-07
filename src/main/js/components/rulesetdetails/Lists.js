import React from "react";
import { List } from 'react-virtualized';

export const Lists = ({lists}) => {

  const liststyle={
  			zIndex: "7",
  			overflowX: "hidden",
  			overflowY: "scroll",
  			height: "200px",
  			width: "400px",
  			padding: "5px"
  	}


  return (
    <div>
      {Object.keys(lists).map((listName, index1) => (
        <div key={"list_"+index1} style={{paddingBottom: "15px"}}>
          <i className="fas fa-list"></i> <b>{listName}</b> ({lists[listName].length})
          <div style={{paddingLeft: "20px"}}>
            {lists[listName].length<10 &&
              <div style={{width:"400px"}}>
                {lists[listName].map((item, index2) => (
                  <div key={index1+"-"+index2}>
                  {item}<br />
                  </div>
                ))}
              </div>
            }
            {lists[listName].length>=10 &&
              <List
            	width={400}
            	height={200}
            	//rowCount={filteredDealers.length}
            	rowCount={lists[listName].length}
            	rowHeight={20}
            	style={liststyle}
            	rowRenderer={({
            			  key,         // Unique key within array of rows
            			  index,       // Index of row within collection
            			  isScrolling, // The List is currently being scrolled
            			  isVisible,   // This row is visible within the List (eg it is not an overscanned row)
            			  style        // Style object to be applied to row (to position it)
            			}) => {
                    return (
                    <div key={key} style={style}>
                      {lists[listName][index]}
                    </div>
                  )
                  }} />
              }
          </div>
        </div>
      ))}
    </div>
  );
};
